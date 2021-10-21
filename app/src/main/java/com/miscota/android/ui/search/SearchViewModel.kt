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

    var showLoading: MutableLiveData<Boolean> = MutableLiveData(false)

    private val _topProductList: MutableLiveData<List<CategoryUiModel.Product>> =
        MutableLiveData(listOf())
    val topProductList: LiveData<List<CategoryUiModel.Product>> = _topProductList

    private val _messageEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val messageEvent: LiveData<Event<String>> = _messageEvent

    private var searchQuery: String? = null

    private var searchJob: Job? = null

    init {
        //loadTopProducts(type = autoStore.getType()!!)
    }

    fun search(query: String, type: String) {
        searchQuery = query
        loadTopProducts(type)
    }

    private fun loadTopProducts(type: String) {
        searchJob?.cancel()
        println("chibato 1 loadMoreTopProducts $type")
        searchJob = viewModelScope.launch {
            delay(300)
            _topProductList.value = mutableListOf()
            val result = runCatching {
                val response = productRepository.searchProducts(
                    searchQuery ?: "", type = type, autoStore.getRetailID()?:"0"
                )
                // for fake searching
//                productRepository.fetchProductsByCategory(
//                    categoryId = 2,
//                    page = 0,
//                    limit = 450,
//                )
                println("chibato 2 loadMoreTopProducts $type")
                if (response.isEmpty()) {
                    return@launch
                }
                println("chibato 3 loadMoreTopProducts $type")
                val list = _topProductList.value?.toMutableList() ?: mutableListOf()
                list.addAll(response.map { it.toCategoryProductUiModel() })
                _topProductList.value = list

                topProductPageNumber++
                shouldTopProductsFetch = false
                showLoading.value = true
                println("chibato 4 loadMoreTopProducts $type")
            }

            val exception = result.exceptionOrNull()
            if (exception != null && exception !is CancellationException) {
                Timber.e(exception.message.toString())
                println(exception.message.toString())
                _messageEvent.value = Event("algo no ha ido bien")
                println("chibato 5 loadMoreTopProducts exception $type")
            }
        }
        println("chibato 6 loadMoreTopProducts $type")
    }

    fun loadMoreTopProducts(type: String) {
        if (shouldTopProductsFetch) {
            shouldTopProductsFetch = false
            loadTopProducts(type)
            println("chibato 0 loadMoreTopProducts $type")
        }
    }

    fun getType():String?{
        var productType = autoStore.getType()
        return productType
    }

    fun setRetailId(retailID: String){
        autoStore.setRetailID(retailID)
    }
}