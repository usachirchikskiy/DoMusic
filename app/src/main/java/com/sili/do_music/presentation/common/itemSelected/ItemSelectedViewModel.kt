package com.sili.do_music.presentation.common.itemSelected

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sili.do_music.business.interactors.common.SearchItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ItemSelectedViewModel @Inject constructor(
    private val searchItem: SearchItem
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

}
