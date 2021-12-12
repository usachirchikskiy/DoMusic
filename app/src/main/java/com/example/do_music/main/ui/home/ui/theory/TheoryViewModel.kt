package com.example.do_music.main.ui.home.ui.theory

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.do_music.interactors.AddToFavourite
import com.example.do_music.interactors.SearchTheory
import com.example.do_music.model.TheoryInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


private const val TAG = "TheoryViewModel"

@HiltViewModel
class TheoryViewModel @Inject constructor(
    private val searchBooks: SearchTheory,
    private val update: AddToFavourite
) : ViewModel() {
    val state: MutableLiveData<TheoryState> = MutableLiveData(TheoryState())
    val favourite: MutableLiveData<Boolean> = MutableLiveData()

    init {
        getpage()
    }

    private fun clearlist() {
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

    private fun pagetozero() {
        this.state.value = state.value?.copy(page = 0)
    }

    private fun incrementpage() {
        state.value?.let { state ->
            this.state.value = state.copy(page = state.page + 1)
        }
    }

    fun getBook(position: Int): TheoryInfo? {
        return state.value?.let {
            this.state.value!!.books[position]
        }
    }

    fun isLiked(position: Int, bindRec: Boolean = false) {
        state.value?.let {
            setPosition(position)
            val isFavourite = this.state.value!!.books[position].isFavourite
            this.state.value!!.books[position].isFavourite = isFavourite != true
            this.state.value?.let {
                it.books[position].isFavourite?.let { it1 ->
                    Log.d(TAG, "isLiked: " + it1)
                    update.execute(
                        bookId = it.books[position].bookId,
                        isFavourite = it1
                    )
                        .onEach { resource ->
                            if (bindRec) {
                                resource.data?.let {
                                    favourite.value = true
                                }
                            }
                        }
                        .launchIn(viewModelScope)
                }
            }
        }


    }

    fun getpage(next: Boolean = false) {
        if (next) {
            incrementpage()
        } else {
            pagetozero()
            clearlist()
        }
        state.value?.let { state ->
            searchBooks.execute(
                page = state.page,
                bookType = state.bookType,
                searchText = state.searchText
            ).onEach {
                this.state.value = state.copy(isLoading =  it.isLoading)
                it.data?.let { list ->
                    this.state.value = state.copy(books = list)
                }
                this.state.value = state.copy(error =  it.error)
            }.launchIn(viewModelScope)
        }
    }

    private fun setPosition(position: Int) {
        state.value?.let { state ->
            this.state.value = state.copy(position = position)
        }
    }

}