package com.example.do_music.main.ui.home.ui.vocals

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.do_music.interactors.AddToFavourite
import com.example.do_music.interactors.SearchVocals
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

private const val TAG = "VocalsViewModel"

@HiltViewModel
class VocalsViewModel @Inject constructor(
    private val searchVocals: SearchVocals,
    private val update: AddToFavourite
) : ViewModel() {
    val state: MutableLiveData<VocalsState> = MutableLiveData(VocalsState())
    val isUpdated: MutableLiveData<Boolean> = MutableLiveData(false)


    init {
        getPage()
    }


    fun getPage(next: Boolean = false, update: Boolean = false) {
        if (next) {
            incrementpagevocalsnotes()
        } else {
            if (!update) {
                pagetozerovocalsnotes()
                clearlistvocalsnotes()
            }
        }
        state.value?.let { state ->
            searchVocals.execute(
                searchText = state.searchText,
                page = state.pageNumber,
                update = update
            ).onEach {

                if (!update) {
                    this.state.value = state.copy(isLoading = it.isLoading)
                }

                it.error?.let { error ->
                    this.state.value = state.copy(error = it.error)
                }

                it.data?.let { instruments ->
                    this.state.value = state.copy(instruments = instruments)
                }


            }.launchIn(viewModelScope)
        }
    }


    private fun incrementpagevocalsnotes() {
        state.value?.let { state ->
            this.state.value =
                state.copy(pageNumber = state.pageNumber + 1)
        }
    }

    private fun clearlistvocalsnotes() {
        state.value?.let { state ->
            this.state.value = state.copy(instruments = listOf())
        }
    }

    private fun pagetozerovocalsnotes() {
        state.value?.let { state ->
            this.state.value = state.copy(pageNumber = 0)
        }
    }

    fun setSearchTextVocalsNotes(search: String) {
        state.value?.let { state ->
            this.state.value = state.copy(searchText = search)
        }
    }

    fun setLoadingToFalse() {
        state.value?.let { state ->
            this.state.value = state.copy(isLoading = false)
        }
    }

    fun isLikedVocalsNotes(favId: Int, isFav: Boolean) {
        state.value?.let { state ->
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
                property = "vocalsId"
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
