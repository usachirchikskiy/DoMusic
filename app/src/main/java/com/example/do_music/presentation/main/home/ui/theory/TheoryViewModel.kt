package com.example.do_music.presentation.main.home.ui.theory

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.do_music.business.interactors.common.AddToFavourite
import com.example.do_music.business.interactors.home.SearchTheory
import com.example.do_music.presentation.session.SessionManager
import com.example.do_music.util.Constants
import com.example.do_music.util.Constants.Companion.BOOK_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


private const val TAG = "TheoryViewModel"

@HiltViewModel
class TheoryViewModel @Inject constructor(
    private val searchBooks: SearchTheory,
    private val update: AddToFavourite,
    private val sessionManager: SessionManager
) : ViewModel() {
    val state: MutableLiveData<TheoryState> = MutableLiveData(TheoryState())
//    val isUpdated: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLastPage: MutableLiveData<Boolean> = MutableLiveData(false)
    private var getTheoryJob: Job? = null

    init {
        Log.d(TAG, "INIT")
        getPage()
    }

    private fun clearList() {
        state.value?.let { state ->
            this.state.value = state.copy(books = listOf())
        }
    }

//    fun setLoadingToFalse() {
//        this.state.value = state.value?.copy(isLoading = false)
//    }

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

    fun isLiked(bookId: Int, isFav: Boolean) {
        state.value?.let { state ->

            update.execute(
                id = bookId,
                isFavourite = isFav,
                property = BOOK_ID
            ).onEach {

                it.data?.let {
//                    isUpdated.value = true
                }

                it.error?.let { error ->
                    this.state.value = state.copy(error = error)
                }

            }.launchIn(viewModelScope)
        }
    }

    fun getPage(next: Boolean = false, update: Boolean = false) {
        getTheoryJob?.cancel()
        if (next) {
            incrementPage()
        } else {
            if (!update) {
                pageToZero()
                clearList()
                isLastPage.value = false
            }
        }
        state.value?.let { state ->
            getTheoryJob = searchBooks.execute(
                page = state.page,
                bookType = state.bookType,
                searchText = state.searchText,
                update = update
            ).onEach {

                if (!update) this.state.value = state.copy(isLoading = it.isLoading)

                it.data?.let { list ->
                    this.state.value = state.copy(
                        books = list,
                        isLoading = false
                    )
                }

                it.error?.let { error ->
                    if (error.message == Constants.LAST_PAGE) {
                        isLastPage.value = true
                    } else {
                        this.state.value = state.copy(error = error)
                    }
                }

            }.launchIn(viewModelScope)
        }
    }
}