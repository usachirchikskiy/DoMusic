package com.example.do_music.main.ui.home.ui.itemSelected

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.do_music.interactors.AddToFavourite
import com.example.do_music.interactors.SearchItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@HiltViewModel
class ItemSelectedViewModel @Inject constructor(
    private val searchItem: SearchItem,
    private val update: AddToFavourite
) : ViewModel() {

    val state: MutableLiveData<ItemState> = MutableLiveData(ItemState())
    val isUpdated: MutableLiveData<Boolean> = MutableLiveData(false)

    fun getItem(itemId: Int, fragmentName: String) {
        state.value?.let { state ->
            searchItem.execute(
                itemId = itemId,
                type = fragmentName
            ).onEach {

                it.data?.let { itemState ->
                    this.state.value = itemState
                }

                it.error?.let { error ->
                    this.state.value = state.copy(error = it.error)
                }

            }.launchIn(viewModelScope)
        }
    }

    fun isLiked(favId: Int, isFav: Boolean, property: String) {
        state.value?.let { state ->
            var favouriteId = -1
            var bookId = -1
            var noteId = -1
            var vocalsId = -1

            if (isFav) {
                if (property == "bookId") {
                    bookId = favId
                } else if (property == "noteId") {
                    noteId = favId
                } else {
                    vocalsId = favId
                }
            }
            if (!isFav) {
                favouriteId = favId
            }
            update.execute(
                bookId = bookId,
                noteId = noteId,
                vocalsId = vocalsId,
                favouriteId = favouriteId,
                isFavourite = isFav,
                property = property
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


}