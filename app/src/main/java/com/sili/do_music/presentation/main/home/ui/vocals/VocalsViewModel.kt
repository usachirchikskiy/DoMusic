package com.sili.do_music.presentation.main.home.ui.vocals

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sili.do_music.business.interactors.home.SearchVocals
import com.sili.do_music.presentation.session.SessionManager
import com.sili.do_music.util.Constants.Companion.LAST_PAGE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@HiltViewModel
class VocalsViewModel @Inject constructor(
    private val searchVocals: SearchVocals,
    private val sessionManager: SessionManager
) : ViewModel() {
    val state: MutableLiveData<VocalsState> = MutableLiveData(VocalsState())
    private var getVocalsJob: Job? = null

    init {
        getPage()
    }

    private fun incrementPageVocalsNotes() {
        state.value?.let { state ->
            this.state.value =
                state.copy(pageNumber = state.pageNumber + 1)
        }
    }

    private fun clearListVocalsNotes() {
        state.value?.let { state ->
            this.state.value = state.copy(instruments = listOf())
        }
    }

    private fun pageToZeroVocalsNotes() {
        state.value?.let { state ->
            this.state.value = state.copy(pageNumber = 0)
        }
    }

    fun setSearchTextVocalsNotes(search: String) {
        state.value?.let { state ->
            this.state.value = state.copy(searchText = search)
        }
    }

    fun clearSessionValues() {
        sessionManager.clearValuesOfDataStore()
    }

    fun setErrorNull() {
        state.value?.let { state ->
            this.state.value = state.copy(error = null)
        }
    }

    fun getPage(next: Boolean = false) {
        if (next) {
            incrementPageVocalsNotes()
        } else {
            pageToZeroVocalsNotes()
            clearListVocalsNotes()
            state.value?.let { state ->
                this.state.value = state.copy(isLastPage = false)
            }
        }

        getVocalsJob?.cancel()
        state.value?.let { state ->
            getVocalsJob = searchVocals.execute(
                searchText = state.searchText,
                page = state.pageNumber
            ).onEach {
                this.state.value = state.copy(isLoading = it.isLoading)

                it.error?.let { error ->
                    if (error.message == LAST_PAGE) {
                        this.state.value = state.copy(isLastPage = true)
                    } else {
                        this.state.value = state.copy(error = error)
                    }
                }

                it.data?.let { instruments ->
                    this.state.value = state.copy(
                        instruments = instruments,
                        isLoading = false
                    )
                }

            }.launchIn(viewModelScope)
        }
    }
}
