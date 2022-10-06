package com.sili.do_music.presentation.main.home.ui.compositors.compositorSelected

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sili.do_music.business.interactors.home.SearchCompositorSelected
import com.sili.do_music.presentation.session.SessionManager
import com.sili.do_music.util.Constants
import com.sili.do_music.util.Constants.Companion.INSTRUMENTAL_GROUP
import com.sili.do_music.util.Constants.Companion.NOTE_ID
import com.sili.do_music.util.Constants.Companion.VOCALS_ID
import com.sili.do_music.util.Constants.Companion.VOCAL_GROUP
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@HiltViewModel
class HomeCompositorSelectedViewModel
@Inject constructor(
    private val searchCompositorSelected: SearchCompositorSelected,
    private val sessionManager: SessionManager
) : ViewModel() {

    private var getSelectedCompositorJob: Job? = null
    val compositorSelectedState: MutableLiveData<CompositorSelectedState> =
        MutableLiveData(CompositorSelectedState())

    init {
        compositorSelectedState.value?.let { state ->
            this.compositorSelectedState.value =
                state.copy(isLoading = true)
        }
    }

    fun setCompositorId(compositorId: Int) {
        compositorSelectedState.value?.let { state ->
            this.compositorSelectedState.value = state.copy(compositorId = compositorId)
        }
    }

    fun getNotesGroupTypes(compositorId: Int) {
        compositorSelectedState.value?.let { state ->
            searchCompositorSelected.getNoteGroupTypes(compositorId).onEach {

                it.data?.let { list ->
                    this.compositorSelectedState.value = state.copy(groupFilters = list)
                    if (list.contains(INSTRUMENTAL_GROUP)) {
                        compositorSelectedState.value!!.filterSelected = INSTRUMENTAL_GROUP
                    } else {
                        compositorSelectedState.value!!.filterSelected = VOCAL_GROUP
                    }
                    getNotesByCompositorSelected()
                }
                it.error?.let { error ->
                    this.compositorSelectedState.value = state.copy(error = error)
                }

            }.launchIn(viewModelScope)
        }

    }

    fun getNotesByCompositorSelected(next: Boolean = false) {
        getSelectedCompositorJob?.cancel()
        if (next) {
            incrementPageCompositorsNotes()
        } else {
            pageToZeroCompositorsNotes()
            clearListCompositorsNotes()
            compositorSelectedState.value?.let { state ->
                compositorSelectedState.value = state.copy(isLastPage = false)
            }
        }
        compositorSelectedState.value?.let { state ->
            when (state.filterSelected) {
                INSTRUMENTAL_GROUP -> {
                    getSelectedCompositorJob =
                        searchCompositorSelected.getInstrumentalNotesByCompositors(
                            searchText = state.searchText,
                            pageNumber = state.pageNumber,
                            compositorId = state.compositorId
                        ).onEach {
                            this.compositorSelectedState.value =
                                state.copy(isLoading = it.isLoading)

                            it.data?.let { instruments ->
                                this.compositorSelectedState.value =
                                    state.copy(
                                        instrumentalCompositions = instruments,
                                        isLoading = false
                                    )
                            }

                            it.error?.let { error ->
                                if (error.message == Constants.LAST_PAGE) {
                                    compositorSelectedState.value = state.copy(isLastPage = true)
                                } else {
                                    this.compositorSelectedState.value = state.copy(error = error)
                                }
                            }

                        }.launchIn(viewModelScope)
                }
                VOCAL_GROUP -> {
                    getSelectedCompositorJob = searchCompositorSelected.getVocalNotesByCompositors(
                        searchText = state.searchText,
                        pageNumber = state.pageNumber,
                        compositorId = state.compositorId
                    ).onEach {
                        this.compositorSelectedState.value =
                            state.copy(isLoading = it.isLoading)

                        it.data?.let { vocals ->
                            this.compositorSelectedState.value =
                                state.copy(
                                    vocalCompositions = vocals,
                                    isLoading = false
                                )
                        }

                        it.error?.let { error ->
                            if (error.message == Constants.LAST_PAGE) {
                                compositorSelectedState.value = state.copy(isLastPage = true)
                            } else {
                                this.compositorSelectedState.value = state.copy(error = error)
                            }
                        }

                    }.launchIn(viewModelScope)
                }
            }
        }
    }

    fun setFilterSelected(filterSelected: String) {
        compositorSelectedState.value?.let { state ->
            this.compositorSelectedState.value = state.copy(filterSelected = filterSelected)
        }
    }

    fun setSearchText(searchText: String) {
        compositorSelectedState.value?.let { state ->
            this.compositorSelectedState.value = state.copy(searchText = searchText)
        }
    }

    private fun clearListCompositorsNotes() {
        compositorSelectedState.value?.let { state ->
            if (state.filterSelected == INSTRUMENTAL_GROUP) {
                this.compositorSelectedState.value = state.copy(instrumentalCompositions = listOf())
            } else {
                this.compositorSelectedState.value = state.copy(vocalCompositions = listOf())
            }
        }
    }

    private fun pageToZeroCompositorsNotes() {
        compositorSelectedState.value?.let { state ->
            this.compositorSelectedState.value = state.copy(pageNumber = 0)
        }
    }

    private fun incrementPageCompositorsNotes() {
        compositorSelectedState.value?.let { state ->
            this.compositorSelectedState.value =
                state.copy(pageNumber = state.pageNumber + 1)
        }
    }

    fun isLiked(): String {
        var property = ""
        compositorSelectedState.value?.let {
            if (it.filterSelected == INSTRUMENTAL_GROUP) {
                property = NOTE_ID
            } else {
                property = VOCALS_ID
            }
        }
        return property
    }

    fun clearSessionValues() {
        sessionManager.clearValuesOfDataStore()
    }

    fun setErrorNull() {
        compositorSelectedState.value?.let { state ->
            this.compositorSelectedState.value =
                state.copy(error = null)
        }
    }

}

