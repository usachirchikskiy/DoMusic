package com.example.do_music.main.ui.favourites

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.do_music.interactors.AddToFavourite
import com.example.do_music.interactors.SearchFavourites
import com.example.do_music.model.Favourite
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val TAG = "FavouriteViewModel"

@HiltViewModel
class FavouriteViewModel
@Inject
constructor(
    private val searchFavourites: SearchFavourites,
    private val update: AddToFavourite
) : ViewModel() {

    val state: MutableLiveData<FavouriteState> = MutableLiveData(FavouriteState())
    val isUpdated: MutableLiveData<Boolean> = MutableLiveData(false)

    init {
        Log.d(TAG, "INIT: ")
        getPage()
    }

    fun addFavClass(favouriteId:Int,favoriteClass:String){
        viewModelScope.launch {
            searchFavourites.addFavClass(
                favoriteId = favouriteId,
                favoriteClass = favoriteClass,
                docType = state.value!!.docType
            )
        }
    }

    fun setDocType(docType: String) {
        state.value?.let { state ->
            this.state.value = state.copy(docType = docType)
        }
    }

    fun getPage(next: Boolean = false, update: Boolean = false) {
        if (next) {
            incrementPage()
        } else {
            if(!update) {
                pageToZero()
                clearList()
            }
        }
        state.value?.let { state ->
            searchFavourites.execute(
                pageNumber = state.page,
                docType = state.docType,
                searchText = state.searchText,
                favouriteClass = state.favClass,
                updated = update
            ).onEach {

                if(!update) {
                    this.state.value = state.copy(isLoading = it.isLoading)
                }

                it.data?.let { list ->
                    Log.d(TAG, "getPage: ")
//                            + state.docType+"\n" + state.favClass+"\n" + it)
                    this.state.value = state.copy(favouriteItems = list)
                }

                it.error?.let { error ->
                    this.state.value = state.copy(error = error)
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun clearList() {
        state.value?.let { state ->
            this.state.value = state.copy(favouriteItems = listOf())
        }
    }

    fun setFavClass(favClass: String) {
        state.value?.let { state ->
            this.state.value = state.copy(favClass = favClass)
        }
    }

    fun setSearchText(searchText: String) {
        state.value?.let { state ->
            this.state.value = state.copy(searchText = searchText)
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

    fun isLiked(favId: Int, isFav: Boolean) {
        state.value?.let {
            var property = "-1"
            var noteId = -1
            var bookId = -1
            var favouriteId = -1
            var vocalsId = -1
            if(it.docType =="NOTES"){
                property = "noteId"
                if(isFav){
                    noteId = favId
                }
            }
            else if(it.docType=="BOOK"){
                property = "bookId"
                if(isFav) {
                    bookId = favId
                }
            }
            else{
                property = "vocalsId"
                if(isFav) {
                    vocalsId = favId
                }
            }
            if(!isFav){
                favouriteId = favId
            }
            update.execute(
                bookId = bookId,
                vocalsId = vocalsId,
                noteId = noteId,
                favouriteId = favId,
                isFavourite = isFav,
                property = property
            ).onEach {
                it.data?.let {
                    update.deleteFromFavourite(favouriteId)
                    isUpdated.value = true
                }
            }.launchIn(viewModelScope)
        }
    }
}

