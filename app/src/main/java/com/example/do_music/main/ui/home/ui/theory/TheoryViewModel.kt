package com.example.do_music.main.ui.home.ui.theory

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.do_music.interactors.AddToFavourite
import com.example.do_music.interactors.SearchTheory
import com.example.do_music.model.TheoryInfo
import com.example.do_music.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class TheoryViewModel @Inject constructor(
    private val searchBooks: SearchTheory,
    private val update: AddToFavourite
) : ViewModel() {
    val state: MutableLiveData<TheoryState> = MutableLiveData(TheoryState())

    init {
        getpage()
    }

    private fun clearlist() {
        state.value?.let { state ->
            this.state.value = state.copy(books = listOf())
        }
    }

    fun downloadFile(bookid: String, context: Context, fileName: String) {
        searchBooks.setContext(context = context)
        viewModelScope.launch {
            searchBooks.downloadfile(bookid, fileName)
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
            val isFavourite = this.state.value!!.books[position].isFavourite
            this.state.value!!.books[position].isFavourite = isFavourite != true
//            this.state.value = this.state.value!!.copy(books = this.state.value!!.books)
//            this.state.value = this.state.value!!.books[position].isFavourite?.let { it1 -> it.copy(isLoading = it1) }
            this.state.value?.let {
                it.books[position].isFavourite?.let { it1 ->
                    update.execute(
//                        page = it.page,
//                        bookType = it.bookType,
//                        searchText = it.searchText,
                        bookId = it.books[position].bookId,
                        isFavourite = it1
                    ).onEach { resource ->
                        if (bindRec) {
                            resource.data?.let { list ->
                                Log.d("Resource", "isLiked: " + list)
                                this.state.value = it.copy(isFavourite = true, position = position)
                            }
                        }
                    }.launchIn(viewModelScope)
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
                it.data?.let { list ->
                    this.state.value = state.copy(books = list, isFavourite = false)
                }

            }.launchIn(viewModelScope)
        }
    }

}