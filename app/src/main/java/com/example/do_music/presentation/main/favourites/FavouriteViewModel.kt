package com.example.do_music.presentation.main.favourites

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.do_music.business.interactors.common.AddToFavourite
import com.example.do_music.business.interactors.favourite.SearchFavourites
import com.example.do_music.util.Constants.Companion.BOOK
import com.example.do_music.util.Constants.Companion.BOOK_ID
import com.example.do_music.util.Constants.Companion.LAST_PAGE
import com.example.do_music.util.Constants.Companion.NOTES
import com.example.do_music.util.Constants.Companion.NOTE_ID
import com.example.do_music.util.Constants.Companion.VOCALS_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
//    val isUpdated: MutableLiveData<Boolean> = MutableLiveData(false)
    var isLastPage = false

    private var getFavouriteJob: Job? = null

    init {
        getPage()
    }

    fun setDocType(docType: String) {
        state.value?.let { state ->
            this.state.value = state.copy(docType = docType)
        }
    }

    fun getPage(next: Boolean = false, update: Boolean = false) {
        Log.d(TAG, "getPage: " + state.value!!.docType)
        getFavouriteJob?.cancel()
        if (next) {
            incrementPage()
        } else {
            if (!update) {
                pageToZero()
                clearList()
            }
        }
        state.value?.let { state ->
            getFavouriteJob = searchFavourites.execute(
                pageNumber = state.page,
                docType = state.docType,
                searchText = state.searchText,
                updated = update
            ).onEach {

                if (!update) {
                    this.state.value = state.copy(isLoading = it.isLoading)
                }

                it.data?.let { list ->

                    this.state.value = state.copy(
                        favouriteItems = list,
                        isLoading = false
                    )

                }

                it.error?.let { error ->
                    if (error.message == LAST_PAGE) {
                        isLastPage = true
                    } else {
                        this.state.value = state.copy(error = error)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun clearList() {
        state.value?.let { state ->
            this.state.value = state.copy(favouriteItems = listOf())
        }
    }

//    fun setFavClass(favClass: String) {
//        state.value?.let { state ->
//            this.state.value = state.copy(favClass = favClass)
//        }
//    }

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

    fun isLiked(itemId: Int, isFavourite: Boolean) {

        state.value?.let { state ->
            val property = when (state.docType) {
                NOTES -> {
                    NOTE_ID
                }
                BOOK -> {
                    BOOK_ID
                }
                else -> {
                    VOCALS_ID
                }
            }
            Log.d(TAG, "isLiked: $itemId, $isFavourite,$property")

            update.execute(
                id = itemId,
                isFavourite = isFavourite,
                property = property
            ).onEach {
                it.data?.let {
                    getPage(update=true)
                }

                it.error?.let { error ->
                    this.state.value = state.copy(error = error)
                }
            }.launchIn(viewModelScope)
        }
    }

    fun setErrorNull() {
        state.value?.let { state ->
            this.state.value = state.copy(error = null)
        }
    }
}

