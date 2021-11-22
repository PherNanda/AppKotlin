package com.miscota.android.ui.search

import androidx.lifecycle.*
import com.miscota.android.repository.ProductRepository
import com.miscota.android.ui.category.CategoryUiModel
import com.miscota.android.ui.category.toCategoryProductUiModel
import com.miscota.android.util.AuthStore
import com.miscota.android.util.Event
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class SearchViewModel(
    private val productRepository: ProductRepository,
    private val autoStore: AuthStore
) : ViewModel() {

    private var topProductPageNumber = 1
    private var shouldTopProductsFetch = false

    private var searchProductPageNumber = 0

    var showLoading: MutableLiveData<Boolean> = MutableLiveData(false)

    private val _topProductList: MutableLiveData<List<CategoryUiModel.Product>> =
        MutableLiveData(listOf())
    val topProductList: LiveData<List<CategoryUiModel.Product>> = _topProductList

    private val _messageEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val messageEvent: LiveData<Event<String>> = _messageEvent

    private val _categories: MutableLiveData<List<CategoryUiModel.CategoryListItem.Category>> =
        MutableLiveData(listOf())

    private var _products: MutableLiveData<List<CategoryUiModel.Product>> =
        MutableLiveData(listOf())

    private var searchQuery: String? = null

    private var searchJob: Job? = null

    /**init {
        loadTopProducts(type = autoStore.getType()!!)
    }**/

    val dataList: LiveData<List<CategoryUiModel>> =
        MediatorLiveData<List<CategoryUiModel>>().apply {
            fun updateList() {

                val products = _products.value ?: listOf()

                val mergedList = mutableListOf<CategoryUiModel>()

                //mergedList.add(featureProduct)
                mergedList.add(CategoryUiModel.TextItem)

                mergedList.addAll(
                    products
                )
                mergedList.add(CategoryUiModel.SpacerItem)

                value = mergedList
            }

            addSource(_categories) {
                updateList()
            }

            addSource(_topProductList) {
                updateList()
            }

            addSource(_products) {
                updateList()
            }
        }

    fun search(query: String, type: String) {
        if (query.length < 3){
            _topProductList.value = mutableListOf()
            searchProductPageNumber = 0
            searchQuery = ""
        }else {
            searchQuery = query
            loadTopProducts(type)
            autoStore.setType(type)
        }
    }

    private fun loadTopProducts(type: String) {

        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(300)
            //_topProductList.value = mutableListOf()
            val result = runCatching {
                val response = productRepository.searchProducts(
                    searchQuery ?: "", type = type,
                    autoStore.getRetailID()?:"0",
                    position = searchProductPageNumber,
                    limit = PAGE_LIMIT
                )

                if (response.isEmpty()) {
                    return@launch
                }

                val list = _topProductList.value?.toMutableList() ?: mutableListOf()
                list.addAll(response.map { it.toCategoryProductUiModel() })

                _topProductList.value = list
                //_products.value = list

                topProductPageNumber++
                searchProductPageNumber += PAGE_LIMIT
                shouldTopProductsFetch = false
                showLoading.value = true
            }

            val exception = result.exceptionOrNull()
            if (exception != null && exception !is CancellationException) {
                Timber.e(exception.message.toString())
                println(exception.message.toString())
                showLoading.value = true
                _messageEvent.value = Event("No hay más productos que mostrar")
            }
        }
    }

    fun loadMoreTopProducts(type: String) {
        if (searchQuery?.length!! < 3){
            _topProductList.value = mutableListOf()
            searchProductPageNumber = 0
            //showLoading.value = false
        }else {
            loadTopProducts(type)
            autoStore.setType(type)
            showLoading.value = false
        }

        //loadTopProducts(type)
        //loadProducts(type)
        if (shouldTopProductsFetch) {
            shouldTopProductsFetch = false
            //loadTopProducts(type)
        }
    }

    fun getType(): String? {
        return autoStore.getType()
    }

    fun setRetailId(retailID: String){
        autoStore.setRetailID(retailID)
    }

    companion object {
        const val PAGE_LIMIT = 20
    }
}