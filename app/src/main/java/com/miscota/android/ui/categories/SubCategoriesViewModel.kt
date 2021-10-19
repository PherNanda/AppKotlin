package com.miscota.android.ui.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.miscota.android.util.Event

class SubCategoriesViewModel(
) : ViewModel() {

    private val _categories: MutableLiveData<List<MainCategoriesUiModel>> =
        MutableLiveData(listOf())
    val dataList: LiveData<List<MainCategoriesUiModel>> =
        MediatorLiveData<List<MainCategoriesUiModel>>().apply {
            fun updateList() {
                val mergedList = mutableListOf<MainCategoriesUiModel>()
                val categories = _categories.value ?: listOf()

                mergedList.addAll(categories)

                value = mergedList
            }

            addSource(_categories) {
                updateList()
            }
        }

    private val _messageEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val messageEvent: LiveData<Event<String>> = _messageEvent

    fun loadSubCategories(list: List<MainCategoriesUiModel>) {
        _categories.value = list
    }

}