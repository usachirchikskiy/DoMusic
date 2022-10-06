package com.sili.do_music.presentation.main.favourites

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sili.do_music.business.interactors.favourite.SearchFavourites
import com.sili.do_music.util.Constants.Companion.LAST_PAGE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class FavouriteViewModel
@Inject
constructor(
    private val searchFavourites: SearchFavourites
) : ViewModel() {

    val state: MutableLiveData<FavouriteState> = MutableLiveData(FavouriteState())
    private var getFavouriteJob: Job? = null

    init {
        getPage()
    }

    fun setDocType(docType: String) {
        state.value?.let { state ->
            this.state.value = state.copy(docType = docType)
        }
    }

    fun getPage(next: Boolean = false) {
        getFavouriteJob?.cancel()
        if (next) {
            incrementPage()
        } else {
            pageToZero()
            clearList()
            state.value?.let { state ->
                this.state.value = state.copy(isLastPage = true)
            }
        }
        state.value?.let { state ->
            getFavouriteJob = searchFavourites.execute(
                pageNumber = state.page,
                docType = state.docType,
                searchText = state.searchText
            ).onEach {

                this.state.value = state.copy(isLoading = it.isLoading)

                it.data?.let { list ->

                    this.state.value = state.copy(
                        favouriteItems = list,
                        isLoading = false
                    )

                }

                it.error?.let { error ->
                    if (error.message == LAST_PAGE) {
                        this.state.value = state.copy(isLastPage = true)
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

    fun setErrorNull() {
        state.value?.let { state ->
            this.state.value = state.copy(error = null)
        }
    }
}

