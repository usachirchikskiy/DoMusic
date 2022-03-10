package com.example.do_music.presentation.common.itemSelected

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.do_music.business.interactors.common.AddToFavourite
import com.example.do_music.business.interactors.common.SearchItem
import com.example.do_music.util.Constants.Companion.BOOK_ID
import com.example.do_music.util.Constants.Companion.NOTE_ID
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

    val errorState: MutableLiveData<Throwable> = MutableLiveData(Throwable())


    fun getItem(itemId: Int, fragmentName: String) {
        state.value?.let {
            searchItem.execute(
                itemId = itemId,
                type = fragmentName
            ).onEach {

                it.data?.let { itemState ->
                    this.state.value = itemState
                }

                it.error?.let { error ->
                    errorState.value = error
                }

            }.launchIn(viewModelScope)
        }
    }

    fun isLiked(favId: Int, isFav: Boolean, property: String) {
        state.value?.let {
//            var favouriteId = -1
//            var bookId = -1
//            var noteId = -1
//            var vocalsId = -1
//            var id = -1

//            if (isFav) {
//                when (property) {
//                    BOOK_ID -> {
//                        id = favId
//                    }
//                    NOTE_ID -> {
//                        noteId = favId
//                    }
//                    else -> {
//                        vocalsId = favId
//                    }
//                }
//            }
//            if (!isFav) {
//                favouriteId = favId
//            }
            update.execute(
                id = favId,
                isFavourite = isFav,
                property = property
            ).onEach {

                it.data?.let {
//                    isUpdated.value = true
                }

                it.error?.let { error ->
                    errorState.value = error
                }

            }.launchIn(viewModelScope)
        }
    }

}
