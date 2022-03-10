package com.example.do_music.presentation.main.home.ui.instruments

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.do_music.business.interactors.common.AddToFavourite
import com.example.do_music.business.interactors.home.SearchInstruments
import com.example.do_music.business.datasources.network.main.home.InstrumentByGroup
import com.example.do_music.presentation.main.home.adapter.InstrumentHelper
import com.example.do_music.presentation.session.SessionManager
import com.example.do_music.util.Constants
import com.example.do_music.util.Constants.Companion.FILTERS_DEFAULT
import com.example.do_music.util.Constants.Companion.FILTERS_DEFAULT_ENSEMBLE
import com.example.do_music.util.Constants.Companion.FILTERS_RU
import com.example.do_music.util.Constants.Companion.FILTERS_RU_ENSEMBLE
import com.example.do_music.util.Constants.Companion.FILTERS_UZ
import com.example.do_music.util.Constants.Companion.FILTERS_UZ_ENSEMBLE
import com.example.do_music.util.Constants.Companion.NOTE_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

private const val TAG = "InstrumentsViewModel"

@HiltViewModel
class InstrumentsViewModel @Inject constructor(
    private val searchInstruments: SearchInstruments,
    private val update: AddToFavourite,
    private val sessionManager: SessionManager
) : ViewModel() {
    val state: MutableLiveData<InstrumentState> = MutableLiveData(InstrumentState())
//    val isUpdated: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLastPage: MutableLiveData<Boolean> = MutableLiveData(false)
    private val instrumentsGroup: MutableLiveData<List<InstrumentByGroup>> = MutableLiveData(listOf())
    private var getInstrumentsJob: Job? = null

    private fun getFilters(lang: String): ArrayList<InstrumentHelper> {
        return when (lang) {
            "ru" -> {
                FILTERS_RU
            }
            "en" -> {
                FILTERS_DEFAULT
            }
            else -> {
                FILTERS_UZ
            }
        }
    }

    private fun getFiltersEnsembles(lang: String): ArrayList<InstrumentHelper> {
        return when (lang) {
            "ru" -> {
                FILTERS_RU_ENSEMBLE
            }
            "en" -> {
                FILTERS_DEFAULT_ENSEMBLE
            }
            else -> {
                FILTERS_UZ_ENSEMBLE
            }
        }
    }

    private fun setFilters(listGroup: List<InstrumentHelper>) {
        Log.d(TAG, "setFilters: " + listGroup.toString())
        state.value?.let { state ->
            this.state.value = state.copy(instrumentsGroup = listGroup)
        }
    }

    fun setFiltersInit(lang: String) {
        when (lang) {
            "ru" -> {
                setFilters(FILTERS_RU)
            }
            "en" -> {
                setFilters(FILTERS_DEFAULT)
            }
            "uz" -> {
                setFilters(FILTERS_UZ)
            }
        }
    }

    private fun clearList() {
        state.value?.let { state ->
            this.state.value = state.copy(instruments = listOf())
        }
    }

    private fun setInstrumentId(id: Int) {
        state.value?.let { state ->
            this.state.value = state.copy(instrumentId = id)
        }
    }

    private fun instrumentGroupName(instrumentGroupName: String) {
        state.value?.let { state ->
            this.state.value = state.copy(instrumentGroupName = instrumentGroupName)
        }
    }

    private fun noteGroupType(noteGroupType: String) {
        state.value?.let { state ->
            this.state.value = state.copy(noteGroupType = noteGroupType)

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

    fun setSearchText(search: String) {
        state.value?.let { state ->
            this.state.value = state.copy(searchText = search)
        }
    }

    private fun setLoadingToFalse() {
        state.value?.let { state ->
            this.state.value = state.copy(isLoading = false)
        }
    }

    fun getInstrumentHelper(position: Int): InstrumentHelper? {
        return state.value?.let {
            this.state.value!!.instrumentsGroup[position]
        }
    }

    fun clearSessionValues(){
        sessionManager.clearValuesOfDataStore()
    }

    fun setErrorNull(){
        state.value?.let { state ->
            this.state.value = state.copy(error = null)
        }
    }

    fun isLiked(favId: Int, isFav: Boolean) {
        state.value?.let { state ->
            update.execute(
                id = favId,
                isFavourite = isFav,
                property = NOTE_ID
            ).onEach {

                it.data?.let {
//                    isUpdated.value = true
//                    getPage(true)
                }

                it.error?.let { error ->
                    this.state.value = state.copy(error = error)
                }

            }.launchIn(viewModelScope)
        }
    }

    fun filterSelected(instrumentHelper: InstrumentHelper, lang: String) {
        state.value?.let { state ->
            val listGroup = arrayListOf<InstrumentHelper>()
            if (instrumentHelper.GroupName != "" && !instrumentHelper.isGroupName) {
                listGroup.add(instrumentHelper.copy(isGroupName = true))
                listGroup.addAll(getFiltersEnsembles(lang))
                instrumentGroupName(instrumentHelper.GroupName)
                searchInstruments.getGroupOfInstruments(instrumentHelper.GroupName).onEach {

                    it.data?.let {
                        instrumentsGroup.value = it
                    }

                    it.error?.let { error ->
                        this.state.value = state.copy(error = error)
                    }

                }.launchIn(viewModelScope)
            } else if (instrumentHelper.Ansamble != "" && !instrumentHelper.isAnsamble) {
                for (i in state.instrumentsGroup) {
                    if (i.isGroupName) {
                        listGroup.add(i)
                    }
                }
                listGroup.add(instrumentHelper.copy(isAnsamble = true))
                for (i in instrumentsGroup.value!!) {
                    listGroup.add(
                        InstrumentHelper(name = i.nameRu, InstumentId = i.id)
                    )
                }
                noteGroupType(instrumentHelper.Ansamble)
            } else if (instrumentHelper.InstumentId != -1 && !instrumentHelper.isInstumentId) {
                setInstrumentId(instrumentHelper.InstumentId)
                for (i in state.instrumentsGroup) {
                    if (i == instrumentHelper) {
                        listGroup.add(instrumentHelper.copy(isInstumentId = true))
                    } else {
                        if (i.isInstumentId) {
                            listGroup.add(i.copy(isInstumentId = false))
                        } else {
                            listGroup.add(i)
                        }
                    }
                }

            } else if (instrumentHelper.isInstumentId) {
                setInstrumentId(-1)
                state.instrumentsGroup.find { it == instrumentHelper }?.isInstumentId = false
                getPage()
                return
            } else if (instrumentHelper.isAnsamble) {
                setInstrumentId(-1)
                noteGroupType("")
                for (i in state.instrumentsGroup) {
                    if (i.isGroupName) {
                        listGroup.add(i)
                    }
                }
                listGroup.addAll(getFiltersEnsembles(lang))
            } else {
                noteGroupType("")
                instrumentGroupName("")
                setInstrumentId(-1)
                listGroup.addAll(getFilters(lang))
            }
            setFilters(listGroup)
            getPage()
        }
    }

    fun getPage(next: Boolean = false, update: Boolean = false) {
        getInstrumentsJob?.cancel()
        if (next) {
            incrementPage()
        } else {
            if (!update) {
                pageToZero()
                clearList()
                isLastPage.value = false
            }
        }
        state.value?.let { state ->
            getInstrumentsJob = searchInstruments.execute(
                instrumentId = state.instrumentId,
                instrumentGroupName = state.instrumentGroupName,
                noteGroupType = state.noteGroupType,
                searchText = state.searchText,
                page = state.page,
                update = update
            ).onEach {
                if (!update) this.state.value = state.copy(isLoading = it.isLoading)

                it.data?.let { list ->
                    this.state.value = state.copy(
                        instruments = list,
                        isLoading = false
                    )
                }
                it.error?.let { error ->
                    if (error.message == Constants.LAST_PAGE) {
                        isLastPage.value = true
                    } else {
                        this.state.value = state.copy(error = error)
                    }
                }

            }.launchIn(viewModelScope)
        }
    }
}