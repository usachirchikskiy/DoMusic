package com.example.do_music.main.ui.home.ui.instruments

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.do_music.interactors.AddToFavourite
import com.example.do_music.interactors.SearchInstruments
import com.example.do_music.main.ui.home.adapter.InstrumentHelper
import com.example.do_music.model.Instrument
import com.example.do_music.network.main.InstrumentByGroup
import com.example.do_music.util.Constants.Companion.FILTERS
import com.example.do_music.util.Constants.Companion.FILTERSANSAMBLE
import com.example.do_music.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val instrumentsGroup: MutableLiveData<List<InstrumentByGroup>> = MutableLiveData()
    val isUpdated: MutableLiveData<Boolean> = MutableLiveData(false)
    val notesByCompositor: MutableLiveData<NotesByCompositorState> =
        MutableLiveData(NotesByCompositorState())
//    val isNoteUpdated: MutableLiveData<Boolean> = MutableLiveData(false)

    init {
        setFilters(FILTERS)
        getpage()
    }


    fun setCompositorId(compositorId: Int) {
        notesByCompositor.value?.let { notesByCompositor ->
            this.notesByCompositor.value = notesByCompositor.copy(compositorId = compositorId)
        }
    }

    fun getNotesByCompositor(next: Boolean = false, update: Boolean = false) {
        if (next) {
            incrementpagecompositorsnotes()
        } else {
            if(!update) {
                pagetozerocompositorsnotes()
                clearlistcompositorsnotes()
            }

        }
        notesByCompositor.value?.let { notesByCompositor ->
            searchInstruments.executeCompositors(
                noteGroupType = notesByCompositor.noteGroupType,
                searchText = notesByCompositor.searchText,
                pageNumber = notesByCompositor.pageNumber,
                compositorId = notesByCompositor.compositorId,
                update = update
            ).onEach {

                this.notesByCompositor.value = notesByCompositor.copy(isLoading = it.isLoading)


                it.data?.let { instruments ->
                    this.notesByCompositor.value = notesByCompositor.copy(instruments = instruments)
                }

                it.error?.let { error ->
                    this.notesByCompositor.value = notesByCompositor.copy(error = error)
                }


            }.launchIn(viewModelScope)
        }
    }

    fun noteGroupTypeCompositor(noteGroupType: String) {
        notesByCompositor.value?.let { notesByCompositor ->
            this.notesByCompositor.value = notesByCompositor.copy(noteGroupType = noteGroupType)
        }
    }

    private fun incrementpagecompositorsnotes() {
        notesByCompositor.value?.let { notesByCompositor ->
            this.notesByCompositor.value =
                notesByCompositor.copy(pageNumber = notesByCompositor.pageNumber + 1)
        }
    }

    private fun clearlistcompositorsnotes() {
        notesByCompositor.value?.let { notesByCompositor ->
            this.notesByCompositor.value = notesByCompositor.copy(instruments = listOf())
        }
    }

    private fun pagetozerocompositorsnotes() {
        notesByCompositor.value?.let { notesByCompositor ->
            this.notesByCompositor.value = notesByCompositor.copy(pageNumber = 0)
        }
    }

    fun setSearchTextCompositorNotes(search: String) {
        notesByCompositor.value?.let { notesByCompositor ->
            this.notesByCompositor.value = notesByCompositor.copy(searchText = search)
        }
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
            } else if (instrumentHelper.Ansamble != "" && !instrumentHelper.isAnsamble) {
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
            } else if (instrumentHelper.InstumentId != -1 && !instrumentHelper.isInstumentId) {
                setInstrumentId(instrumentHelper.InstumentId)
                for (i in state.instrumentsGroup) {
                    if (i == instrumentHelper) {
                        list_group.add(instrumentHelper.copy(isInstumentId = true))
                    } else {
                        if (i.isInstumentId) {
                            list_group.add(i.copy(isInstumentId = false))
                        } else {
                            list_group.add(i)
                        }
                    }
                }

            } else if (instrumentHelper.isInstumentId) {
                Log.d("", "isInstument: " + instrumentHelper.isInstumentId)
                setInstrumentId(-1)
                state.instrumentsGroup.find { it == instrumentHelper }?.isInstumentId = false
                getpage()
                return
            } else if (instrumentHelper.isAnsamble) {
                setInstrumentId(-1)
                Log.d("", "isAnsamble: ")
                noteGroupType("")
                for (i in state.instrumentsGroup) {
                    if (i.isGroupName) {
                        list_group.add(i)
                    }
                }
                list_group.addAll(FILTERSANSAMBLE)
            } else {
                noteGroupType("")
                instrumentGroupName("")
                list_group.addAll(FILTERS)
                Log.d("", "isinstrumentGroup: ")
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

    fun isLiked(favId: Int, isFav: Boolean) {//position: Int, bindRec: Boolean = false) {
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
                property = "noteId"
            ).onEach {
                it.data?.let {
                    isUpdated.value = true
                }
            }.launchIn(viewModelScope)
        }
    }

    fun getpage(next: Boolean = false, update: Boolean = false) {
        if (next) {
            incrementpage()
        } else {
            if (!update) {
                pagetozero()
                clearlist()
            }
        }
        state.value?.let { state ->
            searchInstruments.execute(
                instrumentId = state.instrumentId,
                instrumentGroupName = state.instrumentGroupName,
                noteGroupType = state.noteGroupType,
                searchText = state.searchText,
                page = state.page,
                update = update
            ).onEach {

                this.state.value = state.copy(isLoading = it.isLoading)

                it.data?.let { list ->
                    this.state.value = state.copy(instruments = list)
                }
                it.error?.let { error ->
                    this.state.value = state.copy(error = error)
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

    fun setSearchText(search: String) {
        state.value?.let { state ->
            this.state.value = state.copy(searchText = search)
        }
    }

}