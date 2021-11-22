package com.miscota.android.ui.categories

import androidx.lifecycle.*
import com.miscota.android.repository.CategoryRepository
import com.miscota.android.util.AuthStore
import com.miscota.android.util.Event
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainCategoriesViewModel(
    private val authStore: AuthStore,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private var pageNumber = 1
    private var shouldFetch = false
    private var job: Job? = null

    private val _categories: MutableLiveData<List<MainCategoriesUiModel>> =
        MutableLiveData(listOf())
    val categories: MutableLiveData<List<MainCategoriesUiModel>> = _categories

    lateinit var productType: String
    lateinit var productDetailID: String


    val dataList: LiveData<List<MainCategoriesUiModel>> =
        MediatorLiveData<List<MainCategoriesUiModel>>().apply {

            fun updateList() {
                val mergedList = mutableListOf<MainCategoriesUiModel>()

                val categories = _categories.value ?: listOf()

                mergedList.addAll(categories)
                //_categories.value = mergedList
                value = mergedList
            }

            addSource(_categories) {
                updateList()
            }
        }

    private val _messageEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val messageEvent: LiveData<Event<String>> = _messageEvent

  /** init {
        loadCategories()
    }**/


    private fun loadCategories(forceLoad: Boolean = false,productType: String, requestDetailID: String) {
      _categories.value = listOf()
        if (!forceLoad) {
            val list = _categories.value?.toMutableList() ?: mutableListOf()
            list.add(MainCategoriesUiModel.LoaderItem)
            _categories.value = list
        }
      if (forceLoad) {
          this@MainCategoriesViewModel.productType = productType
          this@MainCategoriesViewModel.productDetailID = requestDetailID
      }
        job = viewModelScope.launch {
            val result = runCatching {
                val response = categoryRepository
                    .fetchCategories(productType, requestDetailID)

                val list = (
                        if (!forceLoad) {
                            (_categories.value
                                ?: listOf()).filter { it !is MainCategoriesUiModel.LoaderItem }
                        } else {
                            listOf()
                        }
                        ).toMutableList()

                if (response.isEmpty()) {
                    _categories.value = list
                    return@launch
                }

                val newList = response.map {
                    it.toUiModel()
                }

                list.addAll(newList)

                _categories.value = list
            }

            val exception = result.exceptionOrNull()
            if (exception != null && exception !is CancellationException) {
                _messageEvent.value = Event(exception.message.toString())

                _categories.value =
                    (_categories.value
                        ?: listOf()).filter { item -> item !is MainCategoriesUiModel.LoaderItem }
            }
        }
    }

    fun loadCategoriesType(productType: String, requestDetailID: String){
        loadCategories(productType = productType, requestDetailID = requestDetailID)
    }

    fun loadMore() {
        if (shouldFetch) {
            shouldFetch = false
            loadCategories(productType = this.productType, requestDetailID = this.productDetailID)
        }
    }

    private fun forceReload() {
        job?.cancel()
        shouldFetch = false
        pageNumber = 1

        loadCategories(forceLoad = true, productType = this.productType, requestDetailID = this.productDetailID)
    }

    companion object {
        const val PAGE_LIMIT = 20
    }

    fun setType(
        type: String,
    ){
        type.let {
            authStore.setType(it)
        }
    }

    fun getType(): String {

        return  authStore.getType()?:" sin tipo??"

    }

    fun setRetailId(
        retailId: String
    ){
        retailId.let {
            authStore.setRetailID(it)
        }
    }

    fun getRetailId(): String{
        return authStore.getRetailID()?: "0"
    }
}