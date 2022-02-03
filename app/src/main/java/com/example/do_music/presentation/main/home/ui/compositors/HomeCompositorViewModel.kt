package com.example.do_music.presentation.main.home.ui.compositors

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.do_music.business.interactors.home.SearchCompositors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


private const val TAG = "HomeCompositorViewModel"

@HiltViewModel
class HomeCompositorViewModel @Inject constructor(
    private val searchCompositors: SearchCompositors
) : ViewModel() {

    val state: MutableLiveData<HomeCompositorsState> = MutableLiveData(HomeCompositorsState())
    private var getCompositorJob: Job? = null

    init {
        Log.d(TAG, ": INIT")
        getPage()
    }

    private fun clearList() {
        state.value?.let { state ->
            this.state.value = state.copy(compositors = listOf())
        }
    }

    fun setLoadingToFalse() {
        this.state.value = state.value?.copy(isLoading = false)
    }

    fun setSearchText(searchText: String) {
        this.state.value = state.value?.copy(searchText = searchText)
    }

    fun setCountryFilter(countryFilter: String) {
        this.state.value = state.value?.copy(country_filter = countryFilter)
    }

    fun getPage(next: Boolean = false) {
        getCompositorJob?.cancel()
        if (next == true) {
            incrementPage()
        } else {
            pageToZero()
            clearList()
        }

        state.value?.let { state ->
            getCompositorJob = searchCompositors.execute(
                page = state.page,
                country_filter = state.country_filter,
                searchText = state.searchText
            ).onEach {

                this.state.value = state.copy(isLoading = it.isLoading)

                it.data?.let { list ->
                    this.state.value = state.copy(
                        compositors = list,
                        isLoading = false
                    )
                }

                it.error?.let { error ->
                    this.state.value = state.copy(error = error)
                }

            }.launchIn(viewModelScope)
        }
    }

    private fun pageToZero() {
        this.state.value = state.value?.copy(page = 0)
    }

    private fun incrementPage() {
        state.value?.let { state ->
            this.state.value = state.copy(page = state.page + 1)
        }
    }
}

