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
                    } else {
                        topProducts
                    }
                )

                val products = _products.value ?: listOf()

                val mergedList = mutableListOf<CategoryUiModel>()
                // FIXME: Hide top products
                //mergedList.add(featureProduct)
                mergedList.add(CategoryUiModel.TextItem)
                mergedList.addAll(if (categories.any { it.isChecked }) {
                    products
                        .filter { product ->
                            categories.any { brandModel ->
                                brandModel.title == product.brand
                                        && brandModel.isChecked
                            }
                        }
                } else {
                    products
                })
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
        //loadProductsCategory()


    }

    private fun loadProducts() {

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
                        retailID = autoStore.getRetailID()?:"5",
                        type = autoStore.getType()!!
                    )

                if (response.isEmpty()) {
                    return@launch
                    //println(" response.isEmpty() autoStore.getRetailID()?.map ${it.retail_shop_id} - ${it.name} ")
                }

                println(" categoryId.toInt()  ${categoryId.id!!.toInt()}")
                val list = _products.value?.toMutableList() ?: mutableListOf()
                list.addAll(response.map { it.toCategoryProductUiModel() })
                println("  map  loadProducts:::: " +
                        "${response.map { 
                            
                            it.toCategoryProductUiModel() 
                        }} ")
                _products.value = list

                productPageNumber += PAGE_LIMIT
                shouldProductsFetch = true
            }

            val exception = result.exceptionOrNull()
            if (exception != null && exception !is CancellationException) {
                Timber.e(exception.message.toString())
                println(exception.message.toString())
                _messageEvent.value = Event("Algo no ha ido bien, no hay productos para mostrar")
                println(" chibato loadProducts() exception _messageEvent.value")
            }
         }

    }

    fun loadMoreProducts() {
        if (shouldProductsFetch) {
            shouldProductsFetch = false
            loadProducts()
        }
    }

    private fun loadTopProducts() {

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
                    _categories.value = brands.distinctBy { it.title }.map { brand ->
                        return@map if (_selectedCategories.any { it.title == brand.title }) {
                            brand.copy(isChecked = true)
                        } else {
                            brand
                        }
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
        println(" chibato 6 in loadTopProducts()")

    }

    fun loadMoreTopProducts() {

        if (shouldTopProductsFetch) {
            shouldTopProductsFetch = false
            loadTopProducts()
        }
    }

    fun forceReload() {
        shouldProductsFetch = false
        productPageNumber = 1
        shouldTopProductsFetch = false
        topProductPageNumber = 1

        loadProducts()
        loadTopProducts()
    }

    fun selectCategory(categoryUiModel: CategoryUiModel.CategoryListItem.Category) {

        println("categoryListItem.categories.size 1 ${categoryListItem.categories.size}")
        for (category in categoryListItem.categories)

            println(" category.title 1 ${category.title}")

        _categories.value?.map {
            println(" select categories.value $it")
        }
        val list = _categories.value?.map {
            if (categoryUiModel.title == it.title) {
                if (categoryUiModel.isChecked) {
                    _selectedCategories.removeAll { brand -> brand.title == categoryUiModel.title }
                } else {
                    _selectedCategories.add(categoryUiModel)
                }
                return@map it.copy(isChecked = !it.isChecked)
            } else {
                return@map it
            }
        }
        _categories.value = list
    }

    fun selectCategoryTwo(categoryUiModel: CategoryUiModel.CategoryListItem.Category) {

        val listTwo = loadProductsCategory(categoryUiModel)

        if (_products.value?.isNotEmpty() == true){
            _products = MutableLiveData(listOf())
            //shouldProductsFetch = false
            //_products.value?.toMutableList()
        }

        println("categoryListItem.categories.size 1 ${categoryListItem.categories.size}")
        for (category in categoryListItem.categories)

            println(" category.title 1 ${category.title}")

        _categories.value?.map {
            println(" select categories.value $it")
        }
        val list = _categories.value?.map {
            if (categoryUiModel.title == it.title) {
                if (categoryUiModel.isChecked) {
                    _selectedCategories.removeAll { brand -> brand.title == categoryUiModel.title }
                } else {
                    _selectedCategories.add(categoryUiModel)
                }
                return@map it.copy(isChecked = !it.isChecked)
            } else {
                return@map it
            }
        }
        _categories.value = list

        //_products.value = list.value
        //_topProductList.value = list.value

    }

    private fun loadProductsCategory(categoryUiModel: CategoryUiModel.CategoryListItem.Category):  MutableLiveData<List<CategoryUiModel.Product>> {
        println("categoryUiModel.isChecked ${categoryUiModel.isChecked}")
        println("categoryUiModel.title ${categoryUiModel.title}")
        println("categoryUiModel.uid ${categoryUiModel.uid}")

            categorys.map {
               // if (it.checked == "true")
                println("it.category ${it.category}")
                    categoryId = CategoryOne(categoryUiModel.uid.toString(),categoryUiModel.uid.toString(),categoryUiModel.title,categoryUiModel.isChecked.toString())
            }

            viewModelScope.launch {
                val result = runCatching {
                    val response =
                        productRepository.fetchProductsByCategory(
                            categoryId = categoryId.id?.toInt()?:0,
                            page = categoryProductPageNumber,
                            limit = PAGE_LIMIT,
                            retailID = autoStore.getRetailID()?:"5",
                            type = autoStore.getType()!!
                        )

                    if (response.isEmpty()) {
                        return@launch
                        //println(" response.isEmpty() autoStore.getRetailID()?.map ${it.retail_shop_id} - ${it.name} ")
                    }

                    println(" categoryId.toInt()  ${categoryId.id!!.toInt()}")


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
                    _messageEvent.value = Event("Algo no ha ido bien, no hay productos para mostrar")
                    println(" chibato loadProductsCategory() exception _messageEvent.value")
                }
            }

        println(" _products::: ${_products.value?.map { it.combinations }}")

        return _products

    }

    /**fun loadMoreProductsCategory() {
        var categoryUiModel = CategoryUiModel.CategoryListItem.Category(0L,"",false)

        if (categoryId.id.isNullOrEmpty()) {

            categorys.map {
                if (it.checked == "true")
                    categoryId = it
                categoryUiModel = it.id?.let { it1 -> it.name?.let { it2 -> CategoryUiModel.CategoryListItem.Category(uid = it1.toLong(), title = it2, isChecked = it.checked.toBoolean()) } }!!
            }
                if (shouldTopProductsFetch) {
                    shouldTopProductsFetch = false
                    loadProductsCategory(categoryUiModel)
                }
        }
        if (!categoryId.id.isNullOrEmpty()){
                categoryUiModel = categoryId.name?.let { CategoryUiModel.CategoryListItem.Category(uid = categoryId.id!!.toLong(),title = it, isChecked = categoryId.checked.toBoolean()) }!!

                if (shouldTopProductsFetch) {
                    shouldTopProductsFetch = false
                    loadProductsCategory(categoryUiModel)
                }
        }


    }**/

    fun selectCategoryItem(categoryUiModel: CategoryUiModel.CategoryListItem) {

        println( " categoryListItem.categories.size ${categoryListItem.categories.size}")
        for (category in categoryListItem.categories)

            println( " category.title ${category.title}")

        _categoriesT.value?.map {
            println(" select categories.value $it")
        }
        val list = _categoriesT.value?.map {

                 println(" categoryUiModel.categories[0].title ${categoryUiModel.categories[0].title}")

                _selectedCategories.add(categoryUiModel.categories[0])

                return@map it

                println(" categoryUiModel.categories[0].title T ${categoryUiModel.categories[0].title}")

        }
        _categoriesT.value = list as List<CategoryUiModel.CategoryListItem>?
    }


    companion object {
        const val PAGE_LIMIT = 6
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