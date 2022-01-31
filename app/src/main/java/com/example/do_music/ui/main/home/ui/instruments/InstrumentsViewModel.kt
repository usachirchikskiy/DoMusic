package com.example.do_music.ui.main.home.ui.instruments

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.do_music.interactors.favourite.AddToFavourite
import com.example.do_music.interactors.home.SearchInstruments
import com.example.do_music.network.main.home.InstrumentByGroup
import com.example.do_music.ui.main.home.adapter.InstrumentHelper
import com.example.do_music.util.Constants.Companion.FILTERS
import com.example.do_music.util.Constants.Companion.FILTERS_ENSEMBLE
import com.example.do_music.util.Constants.Companion.NOTE_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

private const val TAG = "InstrumentsViewModel"

@HiltViewModel
class InstrumentsViewModel @Inject constructor(
    private val searchInstruments: SearchInstruments,
    private val update: AddToFavourite
) : ViewModel() {
    val state: MutableLiveData<InstrumentState> = MutableLiveData(InstrumentState())
    private val instrumentsGroup: MutableLiveData<List<InstrumentByGroup>> =
        MutableLiveData(listOf())
    val isUpdated: MutableLiveData<Boolean> = MutableLiveData(false)
    private var getInstrumentsJob: Job? = null

    init {
        setFilters(FILTERS)
        getPage()
    }

    fun filterSelected(instrumentHelper: InstrumentHelper) {
        state.value?.let { state ->
            val listGroup = arrayListOf<InstrumentHelper>()
            if (instrumentHelper.GroupName != "" && !instrumentHelper.isGroupName) {
                listGroup.add(instrumentHelper.copy(isGroupName = true))
                listGroup.addAll(FILTERS_ENSEMBLE)
                instrumentGroupName(instrumentHelper.GroupName)
                searchInstruments.getGroupOfInstruments(instrumentHelper.GroupName).onEach {

                    it.data?.let {
                        instrumentsGroup.value = it
                    }

                    it.error?.let { error ->
                        this.state.value = state.copy(error = error)
                    }

                }.launchIn(viewModelScope)
            } else if (instrumentHelper.Ansamble != "" && !instrumentHelper.isAnsamble) {
                for (i in state.instrumentsGroup) {
                    if (i.isGroupName) {
                        listGroup.add(i)
                    }
                }
                listGroup.add(instrumentHelper.copy(isAnsamble = true))
                for (i in instrumentsGroup.value!!) {
                    listGroup.add(
                        InstrumentHelper(name = i.nameRu, InstumentId = i.id)
                    )
                }
                noteGroupType(instrumentHelper.Ansamble)
            } else if (instrumentHelper.InstumentId != -1 && !instrumentHelper.isInstumentId) {
                setInstrumentId(instrumentHelper.InstumentId)
                for (i in state.instrumentsGroup) {
                    if (i == instrumentHelper) {
                        listGroup.add(instrumentHelper.copy(isInstumentId = true))
                    } else {
                        if (i.isInstumentId) {
                            listGroup.add(i.copy(isInstumentId = false))
                        } else {
                            listGroup.add(i)
                        }
                    }
                }

            } else if (instrumentHelper.isInstumentId) {
                setInstrumentId(-1)
                state.instrumentsGroup.find { it == instrumentHelper }?.isInstumentId = false
                getPage()
                return
            } else if (instrumentHelper.isAnsamble) {
                setInstrumentId(-1)
                noteGroupType("")
                for (i in state.instrumentsGroup) {
                    if (i.isGroupName) {
                        listGroup.add(i)
                    }
                }
                listGroup.addAll(FILTERS_ENSEMBLE)
            } else {
                noteGroupType("")
                instrumentGroupName("")
                setInstrumentId(-1)
                listGroup.addAll(FILTERS)
            }
            setLoadingToFalse()
            setFilters(listGroup)
            getPage()
        }
    }

    private fun setFilters(listGroup: List<InstrumentHelper>) {
        state.value?.let { state ->
            this.state.value = state.copy(instrumentsGroup = listGroup)
        }
    }


    private fun clearList() {
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

    fun isLiked(favId: Int, isFav: Boolean) {
        state.value?.let { state ->
            var noteId = -1
            var favouriteId = -1
            if (isFav) {
                noteId = favId
            } else {
                favouriteId = favId
            }
            update.execute(
                noteId = noteId,
                favouriteId = favouriteId,
                isFavourite = isFav,
                property = NOTE_ID
            ).onEach {

                it.data?.let {
                    isUpdated.value = true
                }

                it.error?.let { error ->
                    this.state.value = state.copy(error = error)
                }


            }.launchIn(viewModelScope)
        }
    }

    fun getPage(next: Boolean = false, update: Boolean = false) {
        getInstrumentsJob?.cancel()
        if (next) {
            incrementPage()
        } else {
            if (!update) {
                pageToZero()
                clearList()
            }
        }
        state.value?.let { state ->
            getInstrumentsJob = searchInstruments.execute(
                instrumentId = state.instrumentId,
                instrumentGroupName = state.instrumentGroupName,
                noteGroupType = state.noteGroupType,
                searchText = state.searchText,
                page = state.page,
                update = update
            ).onEach {
                if (!update) this.state.value = state.copy(isLoading = it.isLoading)

                it.data?.let { list ->
                    this.state.value = state.copy(instruments = list)
                }
                it.error?.let { error ->
                    this.state.value = state.copy(error = error)
                }

            }.launchIn(viewModelScope)
        }
    }

    private fun pageToZero() {
        state.value?.let { state ->
            this.state.value = state.copy(page = 0)
        }
    }

    private fun incrementPage() {
        state.value?.let { state ->
            this.state.value = state.copy(page = state.page + 1)
        }
    }

    fun setSearchText(search: String) {
        state.value?.let { state ->
            this.state.value = state.copy(searchText = search)
        }
    }

    fun setLoadingToFalse() {
        state.value?.let { state ->
            this.state.value = state.copy(isLoading = false)
        }
    }


}