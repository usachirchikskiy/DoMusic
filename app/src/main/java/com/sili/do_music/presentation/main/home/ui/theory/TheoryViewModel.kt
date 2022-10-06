package com.sili.do_music.presentation.main.home.ui.theory

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sili.do_music.business.interactors.home.SearchTheory
import com.sili.do_music.presentation.session.SessionManager
import com.sili.do_music.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@HiltViewModel
class TheoryViewModel @Inject constructor(
    private val searchBooks: SearchTheory,
    private val sessionManager: SessionManager
) : ViewModel() {
    val state: MutableLiveData<TheoryState> = MutableLiveData(TheoryState())
    private var getTheoryJob: Job? = null

    init {
        getPage()
    }

    private fun clearList() {
        state.value?.let { state ->
            this.state.value = state.copy(books = listOf())
        }
    }


    fun setSearchText(searchText: String) {
        this.state.value = state.value?.copy(searchText = searchText)
    }

    fun setBookType(bookType: String) {
        this.state.value = state.value?.copy(bookType = bookType)
    }

    private fun pageToZero() {
        this.state.value = state.value?.copy(page = 0)
    }

    private fun incrementPage() {
        state.value?.let { state ->
            this.state.value = state.copy(page = state.page + 1)
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
            incrementPage()
        }
        else{
            pageToZero()
            clearList()
            state.value?.let { state ->
                this.state.value = state.copy(isLastPage = false)
            }
        }
        getTheoryJob?.cancel()
        state.value?.let { state ->
            getTheoryJob = searchBooks.execute(
                page = state.page,
                bookType = state.bookType,
                searchText = state.searchText,
            ).onEach {
                this.state.value = state.copy(isLoading = it.isLoading)

                it.data?.let { list ->
                    this.state.value = state.copy(
                        books = list,
                        isLoading = it.isLoading
                    )
                }

                it.error?.let { error ->
                    if (error.message == Constants.LAST_PAGE) {
                        this.state.value = state.copy(isLastPage = true)
                    } else {
                        this.state.value = state.copy(error = error)
                    }
                }

            }.launchIn(viewModelScope)
        }
    }
}