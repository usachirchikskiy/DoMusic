package com.example.do_music.main.ui.home.ui.compositors

import android.util.Log
import androidx.lifecycle.*

import com.example.do_music.interactors.SearchCompositors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


private const val TAG = "HomeCompositorViewModel"

@HiltViewModel
class HomeCompositorViewModel @Inject constructor(
    private val searchCompositors: SearchCompositors
) : ViewModel() {

    val state: MutableLiveData<HomeCompositorsState> = MutableLiveData(HomeCompositorsState())

    init {
        getpage()
    }

    private fun clearlist() {
        state.value?.let { state ->
            this.state.value = state.copy(compositors = listOf())
        }
    }

    fun setSearchText(searchText: String) {
        this.state.value = state.value?.copy(searchText = searchText)
    }

    fun setCountryFilter(countryFilter: String) {
        this.state.value = state.value?.copy(country_filter = countryFilter)
    }

    fun getpage(next: Boolean = false) {
        if (next == true) {
            incrementpage()
        } else {
            pagetozero()
            clearlist()
        }
        state.value?.let { state ->
            searchCompositors.execute(
                page = state.page,
                country_filter = state.country_filter,
                searchText = state.searchText
            ).onEach {

//                this.state.value = state.copy(isLoading = it.isLoading)

                it.data?.let { list ->
                    this.state.value = state.copy(compositors = list)
                }

//                this.state.value = state.copy(error = it.error)


            }.launchIn(viewModelScope)
        }
    }

    private fun pagetozero() {
        this.state.value = state.value?.copy(page = 0)
    }

    private fun incrementpage() {
        state.value?.let { state ->
            this.state.value = state.copy(page = state.page + 1)
        }
    }
}

