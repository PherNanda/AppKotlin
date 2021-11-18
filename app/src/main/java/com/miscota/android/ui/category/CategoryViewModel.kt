package com.miscota.android.ui.category

import androidx.lifecycle.*
import com.miscota.android.repository.ProductRepository
import com.miscota.android.util.AuthStore
import com.miscota.android.util.Event
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import timber.log.Timber

class CategoryViewModel(
    private val categorys:  ArrayList<CategoryOne>,
    private val productRepository: ProductRepository,
    private val autoStore: AuthStore
) : ViewModel() {

    private var productPageNumber = 0
    private var shouldProductsFetch = false

    private var topProductPageNumber = 0
    private var shouldTopProductsFetch = false

    private var categoryProductPageNumber = 0
    //private var shouldCatProductsFetch = false

    private var categoryId: CategoryOne = CategoryOne("","","","")

    var showLoading: MutableLiveData<Boolean> = MutableLiveData(false)

    var showEmpty: MutableLiveData<Boolean> = MutableLiveData(false)

    private val _selectedCategories: MutableList<CategoryUiModel.CategoryListItem.Category> =
        mutableListOf()

    private val _categories: MutableLiveData<List<CategoryUiModel.CategoryListItem.Category>> =
        MutableLiveData(listOf())
    val categories: LiveData<List<CategoryUiModel.CategoryListItem.Category>> = _categories

    private val _categoriesOne: MutableLiveData<List<CategoryOne>> =
        MutableLiveData(listOf())
    val categoriesOne: LiveData<List<CategoryOne>> = _categoriesOne

    private val _categoriesT: MutableLiveData<List<CategoryUiModel.CategoryListItem>> =
        MutableLiveData(listOf())
    val categoriesT: LiveData<List<CategoryUiModel.CategoryListItem>> = _categoriesT

    private val _topProductList: MutableLiveData<List<CategoryUiModel.Product>> =
        MutableLiveData(listOf())
    private var _products: MutableLiveData<List<CategoryUiModel.Product>> =
        MutableLiveData(listOf())

    private val categoryListItem = CategoryUiModel.CategoryListItem(categories = listOf())

    private lateinit var retailID: String
    

    val dataList: LiveData<List<CategoryUiModel>> =
        MediatorLiveData<List<CategoryUiModel>>().apply {
            fun updateList() {
            println("updateList()::: category 33")
                val categories = _categories.value ?: listOf()
                val categoryItem = categoryListItem.copy(
                    categories = categories,
                )

                val topProducts = _topProductList.value ?: listOf()
                val featureProduct = CategoryUiModel.TopProductListItem(
                    products = if (categories.any { it.isChecked }) {
                        topProducts
                            .filter { product ->
                                categories.any { brandModel ->
                                    brandModel.title == product.brand
                                            && brandModel.isChecked
                                }
                            }
                    }else {
                        topProducts
                    }
                )

                val products = _products.value ?: listOf()


                val mergedList = mutableListOf<CategoryUiModel>()
                // FIXME: Hide top products
                //mergedList.add(featureProduct)
                mergedList.add(CategoryUiModel.TextItem)
                /**mergedList.addAll(if (categories.any { it.isChecked }) {
                    products
                        .filter { product ->
                            categories.any { brandModel ->
                                brandModel.title == product.brand
                                        && brandModel.isChecked
                            }
                        }
                } else {
                    products
                })**/

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

    private val _messageEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val messageEvent: LiveData<Event<String>> = _messageEvent

    init {

        if (categoryId.id.isNullOrEmpty()) {
            categorys.map {
                if (it.checked == "true")
                    categoryId = it
            }
        }

        loadTopProducts()
        loadProducts()

    }

    private fun loadProducts() {
        println("updateList()::: category 22")
        if (categoryId.id.isNullOrEmpty()) {
            if (categoryId.id.isNullOrEmpty()) {
                categorys.map {
                    if (it.checked == "true")
                        categoryId = it
                }
            }
        }

        viewModelScope.launch {
            val result = runCatching {
                val response =
                    productRepository.fetchProductsByCategory(
                        categoryId = categoryId.id?.toInt()?:0,
                        page = productPageNumber,
                        limit = PAGE_LIMIT,
                        retailID = autoStore.getRetailID()?:"0",
                        type = autoStore.getType()!!
                    )

                if (response.isEmpty()) {
                    return@launch
                }

                val list = _products.value?.toMutableList() ?: mutableListOf()
                list.addAll(response.map { it.toCategoryProductUiModel() })

                _products.value = list

                productPageNumber += PAGE_LIMIT
                shouldProductsFetch = true
            }

            val exception = result.exceptionOrNull()
            if (exception != null && exception !is CancellationException) {
                Timber.e(exception.message.toString())
                println(exception.message.toString())
                _messageEvent.value = Event("Algo no ha ido bien, no hay productos para mostrar")
            }
         }

    }

    fun loadMoreProducts() {
        println("updateList()::: category 11")
        if (shouldProductsFetch) {
            shouldProductsFetch = false
            loadProducts()
        }
    }

    private fun loadTopProducts() {
        println("updateList()::: category 4")
            viewModelScope.launch {
                val result = runCatching {
                    val response =
                        productRepository.fetchProductsByCategory(
                            categoryId = categoryId.id?.toInt()?:0,
                            page = productPageNumber,
                            limit = 20,
                            retailID =autoStore.getRetailID()?:"5",
                            type = autoStore.getType()!!
                        )

                    if (response.isEmpty()) {
                        return@launch
                    }

                    val brands = _categories.value?.toMutableList() ?: mutableListOf()
                    brands.addAll(response.map { it.toBrandsUiModel() })

                    _categories.value = categorys.mapIndexed { index, categoryOne ->
                       (CategoryUiModel.CategoryListItem.Category(categoryOne.id!!.toLong(), categoryOne.name!!, categoryOne.checked.toBoolean()))
                    }

                    //selectCategory(categoryId)
                    val list = _topProductList.value?.toMutableList() ?: mutableListOf()
                    list.addAll(response.map { it.toCategoryProductUiModel() })
                    _topProductList.value = list

                    topProductPageNumber += PAGE_LIMIT
                    shouldTopProductsFetch = true
                    showLoading.value = shouldTopProductsFetch
                }

                val exception = result.exceptionOrNull()
                if (exception != null && exception !is CancellationException) {
                    Timber.e(exception.message.toString())
                    showLoading.value = true
                    showEmpty.value = true
                    //_messageEvent.value = Event("Algo no ha ido bien, no hay productos para mostrar")
                }
            }

    }

    fun loadMoreTopProducts() {
        println("updateList()::: category 5")
        if (shouldTopProductsFetch) {
            shouldTopProductsFetch = false
            loadTopProducts()
        }
    }

    fun forceReload() {
        println("updateList()::: category 6")
        shouldProductsFetch = false
        productPageNumber = 1
        shouldTopProductsFetch = false
        topProductPageNumber = 1

        loadProducts()
        loadTopProducts()
    }



    fun selectCategoryTwo(
        categoryUiModel: CategoryUiModel.CategoryListItem.Category
    ) {
        println("updateList()::: category 7")
        val listTwo = loadProductsCategory(categoryUiModel)

        val newList = _categories.value?.map {

            if (categoryUiModel.isChecked && it.uid == categoryUiModel.uid )
            {
                return@map it.copy(isChecked = true)
            }
            else{
                return@map it.copy(isChecked = false)
            }
        }

        _products.value = listTwo.value
        _categories.value = newList

    }

    private fun loadProductsCategory(categoryUiModel: CategoryUiModel.CategoryListItem.Category):  MutableLiveData<List<CategoryUiModel.Product>> {
        println("updateList()::: category 8")
        categoryProductPageNumber = 0
            categorys.map {

                    categoryId = CategoryOne(categoryUiModel.uid.toString(),categoryUiModel.uid.toString(),categoryUiModel.title,categoryUiModel.isChecked.toString())
            }
            viewModelScope.launch {
                val result = runCatching {
                    val response =
                        productRepository.fetchProductsByCategory(
                            categoryId = categoryId.id?.toInt()?:0,
                            page = categoryProductPageNumber,
                            limit = PAGE_LIMIT,
                            retailID = autoStore.getRetailID()?:"0",
                            type = autoStore.getType()!!
                        )

                    if (response.isEmpty()) {
                        return@launch
                    }

                    _products.value = listOf()
                    val list = _products.value?.toMutableList() ?: mutableListOf()
                    list.addAll(response.map { it.toCategoryProductUiModel() })
                    println("  map  loadProducts:::: " +
                            "${response.map {

                                it.toCategoryProductUiModel()
                            }} ")
                    _products.value = list
                    //_topProductList.value = list

                    categoryProductPageNumber += PAGE_LIMIT
                    shouldProductsFetch = true
                    showLoading.value = shouldProductsFetch
                }

                val exception = result.exceptionOrNull()
                if (exception != null && exception !is CancellationException) {
                    Timber.e(exception.message.toString())
                    println(exception.message.toString())
                    showEmpty.value = true
                    _messageEvent.value = Event("Algo no ha ido bien, no hay productos para mostrar")
                }
            }

        return _products

    }

    /**fun selectCategoryItem(categoryUiModel: CategoryUiModel.CategoryListItem) {
        for (category in categoryListItem.categories)

        _categoriesT.value?.map {
        }
        val list = _categoriesT.value?.map {

                _selectedCategories.add(categoryUiModel.categories[0])

                return@map it
        }
        _categoriesT.value = list as List<CategoryUiModel.CategoryListItem>?
    }**/


    companion object {
        const val PAGE_LIMIT = 20
    }

    fun setType(
        type: String,
    ){
        type.let {
            autoStore.setType(it)
        }
    }

    fun getType(): String? {

         return autoStore.getType()

    }

}