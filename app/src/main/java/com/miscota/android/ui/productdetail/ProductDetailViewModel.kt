package com.miscota.android.ui.productdetail

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.miscota.android.R
import com.miscota.android.events.EventsManager
import com.miscota.android.models.ProductNetworkModel
import com.miscota.android.repository.CheckoutRepository
import com.miscota.android.repository.ProductRepository
import com.miscota.android.ui.cart.CartUiModel
import com.miscota.android.ui.cart.toCartItemUiModel
import com.miscota.android.util.AuthStore
import com.miscota.android.util.Event
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    val authStore: AuthStore,
    private val productRepository: ProductRepository,
    private val checkoutRepository: CheckoutRepository,
    val eventsManager: EventsManager
): ViewModel(){
    private val _quantity = MutableLiveData(1)
    val quantity: LiveData<Int> = _quantity

    private val _stock = MutableLiveData(-1)
    val stock: LiveData<Int> = _stock

    private val _options =  MutableLiveData<List<OptionUiModel>>()
    val optionss = _options

    var product: ProductNetworkModel? = null
    var productType: String? = null

    private val _selectedCombination = MutableLiveData<OptionUiModel.Option>()
    val selectedCombinations = _selectedCombination

    private val _totalItemsCart = MutableLiveData(0)
    val totalItensCart: LiveData<Int> = _totalItemsCart

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    var selectedCombination: OptionUiModel.Option? = null
        set(value) {
            field = value
            value?.let { option ->

                product?.let {
                        val combinations = product!!.combinations?.filter {
                           it.ref == value.id
                        }

                    if (combinations != null) {
                        _stock.value = combinations[0].stock?.toInt()
                        //product?.combinations?.get(index = 0)?.stock = combinations[0].stock?.toInt()

                    }
                }

            }
        }



    private val _messageEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val messageEvent: LiveData<Event<String>> = _messageEvent

    fun increment() {
        _quantity.value = _quantity.value!! + 1
    }

    fun incrementCart(qtyProduct: Int) {
        _quantity.value = qtyProduct
        println(" _quantity.value ${_quantity.value}")
    }

    fun decrement() {
        if (requireNotNull(_quantity.value) > 1) {
            _quantity.value = quantity.value!! - 1
        }
    }

    fun getProductStock(productId: Long, productType: String) {
        viewModelScope.launch {
            val result = runCatching {
                val response = productRepository.fetchProductById(productId, productType, authStore.getRetailID()?:"0")
                product = response[0]

                if (selectedCombination != null) {
                    selectedCombination = selectedCombination
                }
                else{
                    addCombinationOptions(product = product as Product)

                }
            }
        }

        _selectedCombination.value = selectedCombination
    }

    lateinit var optionAdapter: ProductDetailVariantsAdapter

    fun addCombinationOptions(product: Product) {

        val options = mutableListOf<OptionUiModel>()

        if (product.combinationOptions.size > 1) {
            product.combinationOptions.forEachIndexed { index, combinationOption ->
                options.add(combinationOption)

                if (index - 1 != options.size) {
                    options.add(OptionUiModel.Spacer)
                }
            }

            optionAdapter = ProductDetailVariantsAdapter { optionId ->
                optionAdapter.submitList(
                    options.map {
                        if (it is OptionUiModel.Option) {

                            _options.value = listOf(it)
                            if (it.id == optionId) {
                                it.copy(isChecked = true, productTypeOption = authStore.getType())

                            } else {
                                it.copy(isChecked = false, productTypeOption = authStore.getType())
                            }
                        } else {
                            it
                        }
                    }
                )
            }
            optionAdapter.submitList(options)
        }
    }

    fun addToCart(product: CartProduct, context: Context, options: OptionUiModel.Option?) {

                try {
                    if ( product.combinationStock!! > 0 ) {
                        authStore.addToCart(
                            quantity.value ?: 1,
                            product,
                            selectedCombination = options!!,
                            productType = product.typeProduct,
                            stock = product.combinationStock
                        )

                        println(" product.combinationStock ${product.combinationStock}")
                        println(" options.stock ${options.stock}")

                        val itemToCart = eventsManager.itemToCart(product, product.brand)
                        eventsManager.addToCart(itemToCart, product, quantity.value!!)
                        _totalItemsCart.value = authStore.getTotalCartItens() + quantity.value!!
                        authStore.setTotalCartItens(_totalItemsCart.value!!)
                        authStore.getTotalCartItens()

                        val list = loadCheckout()

                    }
                } catch (e: Exception) {
                    _messageEvent.value = Event(e.message)
                    return
                }
                if (product.combinationStock > 0) {
                    _messageEvent.value = Event(context.getString(R.string.item_added_to_cart))
                    _totalItemsCart.value
                    totalItensCart.value
                }else{
                    _messageEvent.value = Event(context.getString(R.string.item_dont_added_to_cart))
                    _totalItemsCart.value = authStore.getTotalCartItens()
                    totalItensCart.value
                }

    }

    fun isLoggedIn(): Boolean {
        return authStore.isLoggedIn()
    }

    fun loadCheckout(): MutableList<CartUiModel.ItemListCheckout> {

        val list: MutableList<CartUiModel.ItemListCheckout> = mutableListOf()
        authStore.getCart().map {
            it.toCartItemUiModel()
            println(" it items checkout: $it")

            list.add(
                CartUiModel.ItemListCheckout(
                    qty = it.qty.toString(),
                    price = it.combinationPrice.toString(),
                    type = it.type,
                    ref = it.combinationReference,
                )
            )
        }
        return list
    }


    fun getType(): String?{
        productType = authStore.getType()
        return productType
    }

    fun carriersCheckout(clientType: Boolean, items: List<CartUiModel.ItemListCheckout>, retailID: String) {
        println(" START CarriersCheckout ProductDetailViewModel line 209")
        try {
            viewModelScope.launch {
                val result = runCatching {
                    checkoutRepository.fetchCarriersCheckout(
                        requestRetailId = retailID,
                        clientType = clientType,
                        items,

                    )
                }

                println("  cost sameday:: ${result.getOrThrow().sd}")
                println("  cost ecommerce:: ${result.getOrThrow().ecommerce}")
                if (result != null) {
                    //_checkoutResult.value =

                    authStore.setCarriersEco(result.getOrThrow().ecommerce.toString())
                    authStore.setCarriersSd(result.getOrThrow().sd.toString())
                    val carriersTotal = result.getOrThrow().sd.plus(result.getOrThrow().ecommerce)
                    authStore.setCarriers(carriersTotal.toString())
                    //loadCarriers()
                } else {
                    //_checkoutResult.value = CheckoutResult(error = R.string.checkout_failed)
                    println("error")
                }
            }
            println(" END CarriersCheckout ProductDetailViewModel line 238")
        } catch (e: Exception) {
            println("${e.message}  -Error-  ${e.printStackTrace()}")
        }
    }


}