package com.example.do_music.main.ui.home.ui.instruments

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.do_music.interactors.AddToFavourite
import com.example.do_music.interactors.SearchInstruments
import com.example.do_music.main.ui.home.adapter.InstrumentHelper
import com.example.do_music.network.main.InstrumentByGroup
import com.example.do_music.util.Constants.Companion.FILTERS
import com.example.do_music.util.Constants.Companion.FILTERSANSAMBLE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class InstrumentsViewModel @Inject constructor(
    private val searchInstruments: SearchInstruments,
    private val update: AddToFavourite
) : ViewModel() {
    val state: MutableLiveData<InstrumentState> = MutableLiveData(InstrumentState())
    val instrumentsGroup: MutableLiveData<List<InstrumentByGroup>> = MutableLiveData()

    init {
        setFilters(FILTERS)
        getpage(init=true)
    }

    fun filterSelected(instrumentHelper: InstrumentHelper) {
        state.value?.let { state ->
            val list_group = arrayListOf<InstrumentHelper>()
            if (instrumentHelper.GroupName != "" && !instrumentHelper.isGroupName) {
                list_group.add(instrumentHelper.copy(isGroupName = true))
                list_group.addAll(FILTERSANSAMBLE)
                instrumentGroupName(instrumentHelper.GroupName)
                searchInstruments.getGroupOfInstruments(instrumentHelper.GroupName).onEach {
                    it.data?.let {
                        instrumentsGroup.value = it
                    }
                }.launchIn(viewModelScope)
            }
            else if (instrumentHelper.Ansamble != "" && !instrumentHelper.isAnsamble) {
                for (i in state.instrumentsGroup) {
                    if (i.isGroupName) {
                        list_group.add(i)
                    }
                }
                list_group.add(instrumentHelper.copy(isAnsamble = true))
                for (i in instrumentsGroup.value!!) {
                    list_group.add(
                        InstrumentHelper(name = i.nameRu, InstumentId = i.id)
                    )
                }
                noteGroupType(instrumentHelper.Ansamble)
            }
            else if (instrumentHelper.InstumentId != -1 && !instrumentHelper.isInstumentId) {
                setInstrumentId(instrumentHelper.InstumentId)
                for (i in state.instrumentsGroup) {
                    if (i == instrumentHelper){
                        list_group.add(instrumentHelper.copy(isInstumentId = true))
                    }
                    else{
                        if(i.isInstumentId) {
                            list_group.add(i.copy(isInstumentId = false))
                        }
                        else{
                            list_group.add(i)
                        }
                    }
                }

            }
            else if (instrumentHelper.isInstumentId) {
                Log.d("", "isInstument: " + instrumentHelper.isInstumentId)
                setInstrumentId(-1)
                state.instrumentsGroup.find {it==instrumentHelper}?.isInstumentId=false
                getpage()
                return
                }
            else if (instrumentHelper.isAnsamble) {
                Log.d("", "isAnsmble: ")
                noteGroupType("")
                for (i in state.instrumentsGroup) {
                    if (i.isGroupName) {
                        list_group.add(i)
                    }
                }
                list_group.addAll(FILTERSANSAMBLE)
            }
            else {
                instrumentGroupName("")
                list_group.addAll(FILTERS)
            }

            setFilters(list_group)
            getpage()
        }
    }

    private fun setFilters(list_group: List<InstrumentHelper>) {
        state.value?.let { state ->
            this.state.value = state.copy(instrumentsGroup = list_group)
        }
    }


    private fun clearlist() {
        state.value?.let { state ->
            this.state.value = state.copy(instruments = listOf())
        }
    }

    private fun setInstrumentId(id: Int) {
        state.value?.let { state ->
            this.state.value = state.copy(instrumentId = id)
        }
    }

    private fun instrumentGroupName(instrumentGroupName: String) {
        state.value?.let { state ->
            this.state.value = state.copy(instrumentGroupName = instrumentGroupName)
        }
    }

    private fun noteGroupType(noteGroupType: String) {
        state.value?.let { state ->
            this.state.value = state.copy(noteGroupType = noteGroupType)

        }
    }

    fun getInstrumentHelper(position: Int): InstrumentHelper? {
        return state.value?.let {
            this.state.value!!.instrumentsGroup[position]
        }
    }

    fun isLiked(position: Int, bindRec: Boolean = false) {
        state.value?.let {
            val isFavourite = this.state.value!!.instruments[position].isFavourite
            this.state.value!!.instruments[position].isFavourite = isFavourite != true
            this.state.value?.let {
                it.instruments[position].isFavourite?.let { it1 ->
                    update.execute(
                        noteId = it.instruments[position].noteId,
                        isFavourite = it1
                    ).onEach { resource ->
                        if (bindRec) {
                            resource.data?.let { list ->
                                this.state.value = it.copy(isFavourite = true, position = position)
                            }
                        }
                    }.launchIn(viewModelScope)
                }
            }
        }


    }

    fun getpage(next: Boolean = false,init:Boolean = false) {
        if (next) {
            incrementpage()
        } else {
            pagetozero()
            clearlist()
        }
        state.value?.let { state ->
            searchInstruments.execute(
                instrumentId = state.instrumentId,
                instrumentGroupName = state.instrumentGroupName,
                noteGroupType = state.noteGroupType,
                searchText = state.searchText,
                page = state.page,
                init = init
            ).onEach {
                it.data?.let { list ->
                    this.state.value = state.copy(instruments = list, isFavourite = false)
                }

            }.launchIn(viewModelScope)
        }
    }

    private fun pagetozero() {
        state.value?.let { state ->
            this.state.value = state.copy(page = 0)
        }
    }

    private fun incrementpage() {
        state.value?.let { state ->
            this.state.value = state.copy(page = state.page + 1)
        }
    }

    fun setSearchText(search:String){
        state.value?.let { state ->
            this.state.value = state.copy(searchText = search)
        }
    }
}