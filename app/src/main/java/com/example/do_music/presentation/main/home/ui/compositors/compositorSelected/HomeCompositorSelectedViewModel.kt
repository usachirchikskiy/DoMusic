package com.example.do_music.presentation.main.home.ui.compositors.compositorSelected

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.do_music.business.interactors.common.AddToFavourite
import com.example.do_music.business.interactors.home.SearchCompositorSelected
import com.example.do_music.presentation.session.SessionManager
import com.example.do_music.util.Constants.Companion.INSTRUMENTAL_GROUP
import com.example.do_music.util.Constants.Companion.NOTE_ID
import com.example.do_music.util.Constants.Companion.VOCALS_ID
import com.example.do_music.util.Constants.Companion.VOCAL_GROUP
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


private const val TAG = "SelectedViewModel"

@HiltViewModel
class HomeCompositorSelectedViewModel
@Inject constructor(
    private val searchCompositorSelected: SearchCompositorSelected,
    private val update: AddToFavourite,
    private val sessionManager: SessionManager
) : ViewModel() {

    private var getSelectedCompositorJob: Job? = null
    val compositorSelectedState: MutableLiveData<CompositorSelectedState> =
        MutableLiveData(CompositorSelectedState())
    val isUpdated: MutableLiveData<Boolean> = MutableLiveData(false)

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
                    Log.d(TAG, "getNotesGroupTypes: " + list)
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

    fun getNotesByCompositorSelected(next: Boolean = false, update: Boolean = false) {
        getSelectedCompositorJob?.cancel()
        if (next) {
            incrementPageCompositorsNotes()
        } else {
            if (!update) {
                pageToZeroCompositorsNotes()
                clearListCompositorsNotes()
            }
        }
        compositorSelectedState.value?.let { state ->
            when (state.filterSelected) {
                INSTRUMENTAL_GROUP -> {
                    getSelectedCompositorJob =
                        searchCompositorSelected.getInstrumentalNotesByCompositors(
                            searchText = state.searchText,
                            pageNumber = state.pageNumber,
                            compositorId = state.compositorId,
                            update = update
                        ).onEach {
                            if (!update) this.compositorSelectedState.value =
                                state.copy(isLoading = it.isLoading)

                            it.data?.let { instruments ->
                                this.compositorSelectedState.value =
                                    state.copy(
                                        instrumentalCompositions = instruments,
                                        isLoading = false
                                    )
                            }

                            it.error?.let { error ->
                                this.compositorSelectedState.value = state.copy(error = error)
                            }

                        }.launchIn(viewModelScope)
                }
                VOCAL_GROUP -> {
                    getSelectedCompositorJob = searchCompositorSelected.getVocalNotesByCompositors(
                        searchText = state.searchText,
                        pageNumber = state.pageNumber,
                        compositorId = state.compositorId,
                        update = update
                    ).onEach {
                        if (!update) this.compositorSelectedState.value =
                            state.copy(isLoading = it.isLoading)

                        it.data?.let { vocals ->
                            this.compositorSelectedState.value =
                                state.copy(
                                    vocalCompositions = vocals,
                                    isLoading = false
                                )
                        }

                        it.error?.let { error ->
                            this.compositorSelectedState.value = state.copy(error = error)
                        }

                    }.launchIn(viewModelScope)
                }
                else -> {

                }
            }
        }
    }

    private fun instrumentalCompositionLiked(favId: Int, isFav: Boolean) {
        compositorSelectedState.value?.let { state ->
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
                    this.compositorSelectedState.value = state.copy(error = error)
                }


            }.launchIn(viewModelScope)
        }
    }

    private fun vocalCompositionLiked(favId: Int, isFav: Boolean) {
        compositorSelectedState.value?.let { state ->
            var vocalsId = -1
            var favouriteId = -1
            if (isFav) {
                vocalsId = favId
            } else {
                favouriteId = favId
            }
            update.execute(
                vocalsId = vocalsId,
                favouriteId = favouriteId,
                isFavourite = isFav,
                property = VOCALS_ID
            ).onEach {

                it.data?.let {
                    isUpdated.value = true
                }

                it.error?.let { error ->
                    this.compositorSelectedState.value = state.copy(error = error)
                }


            }.launchIn(viewModelScope)
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

    fun setLoadingToFalse() {
        compositorSelectedState.value?.let { state ->
            this.compositorSelectedState.value = state.copy(isLoading = false)
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

    fun isLiked(favId: Int, isFav: Boolean) {
        compositorSelectedState.value?.let {
            if (it.filterSelected == INSTRUMENTAL_GROUP) {
                instrumentalCompositionLiked(favId = favId, isFav = isFav)
            } else {
                vocalCompositionLiked(favId = favId, isFav = isFav)
            }
        }
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

