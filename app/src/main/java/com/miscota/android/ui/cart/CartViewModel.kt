package com.miscota.android.ui.cart

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.adyen.checkout.core.log.LogUtil
import com.google.gson.GsonBuilder
import com.miscota.android.R
import com.miscota.android.events.EventsManager
import com.miscota.android.repository.CheckoutRepository
import com.miscota.android.ui.checkoutpayment.*
import com.miscota.android.util.*
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import java.util.*
import kotlin.collections.ArrayList

class CartViewModel(
    val authStore: AuthStore,
    private val checkoutRepository: CheckoutRepository,
    val eventsManager: EventsManager
) : ViewModel() {
    private val _items: MutableLiveData<List<CartUiModel>> = MutableLiveData(listOf())
    val items: LiveData<List<CartUiModel>> = _items

    private val _total = MutableLiveData(0.0)
    val total: LiveData<Double> = _total

    private val _messageEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val messageEvent: LiveData<Event<String>> = _messageEvent

    private val _checkoutResult = MutableLiveData<CheckoutResult>()
    val checkoutResult: LiveData<CheckoutResult> = _checkoutResult

    lateinit var paymentMethod: PaymentMethod

    private val _costSd = MutableLiveData(0.0)
    val costSd: LiveData<Double> = _costSd

    private val _costEcommerce = MutableLiveData(0.0)
    val costEcommerce: LiveData<Double> = _costEcommerce

    private val _refOrder: MutableLiveData<String> = MutableLiveData()
    val refOrder: LiveData<String> = _refOrder

    private val _costDeliver: MutableLiveData<Double> = MutableLiveData()
    val costDeliver: LiveData<Double> = _costDeliver

    private val _types: MutableLiveData<ArrayList<String>> = MutableLiveData()
    val types: LiveData<ArrayList<String>> = _types

    private var _payment: MutableLiveData<PaymentMethod> = MutableLiveData()
    val payment: LiveData<PaymentMethod> = _payment

    var showLoading: MutableLiveData<Boolean> = MutableLiveData(false)


    init {
        _types.value?.add("")
    }


    fun loadCart() {
        val list: MutableList<CartUiModel> = mutableListOf()
        val cartItems = authStore.getCart().map {
            it.toCartUiModel()
        }

        val subtotal: Double = authStore.getCart().map {
            _types.value?.add(it.type?:"sin tipo")
            it.product.combinationPrice * it.qty
        }.foldRight(0.0) { acc, d -> acc + d }


        _total.value = subtotal

        for (item in cartItems)
            println(" item::::cart $item")

        /*********************************************************************/
        var userType = false
        if (isLoggedIn())
        else
            userType = true

        val items: List<CartUiModel.ItemListCheckout> = loadCartToList()

            viewModelScope.launch {
                val result = runCatching {
                    checkoutRepository.fetchCarriersCheckout(
                        requestRetailId = authStore.getRetailID()?:"0",
                        clientType = userType,
                        items
                    )
                }


                if (result != null) {

                    _checkoutResult.value =
                        CheckoutResult(success = "Chekout Ok")

                    _costSd.value = result.getOrThrow().sd
                    _costEcommerce.value = result.getOrThrow().ecommerce
                    _costDeliver.value = result.getOrThrow().sd.plus(result.getOrThrow().ecommerce)

                    authStore.setCarriersEco(result.getOrThrow().ecommerce.toString())
                    authStore.setCarriersSd(result.getOrThrow().sd.toString())
                    authStore.setCarriers(_costDeliver.value.toString())
                    println("result.getOrThrow().ecommerce.toString()::: ${result.getOrThrow().ecommerce}")
                    println("result.getOrThrow().sd.toString()::: ${result.getOrThrow().sd}")
                    println("_costSd.value::: ${_costSd.value}")
                    println("_costEcommerce.value::: ${_costEcommerce.value}")

                    _total.value = subtotal +  result.getOrThrow().sd + result.getOrThrow().ecommerce
                    loadCarriers()
                } else {
                    _checkoutResult.value = CheckoutResult(error = R.string.checkout_failed)
                }
            }
        /**********************/
        var totalProductsCart = 0
        cartItems.map{
            totalProductsCart += it.quantity
        }
        authStore.setTotalCartItens(totalProductsCart)

        if (cartItems.count() > 0)
            list.addAll(cartItems)
            //totalProducts = cartItems.size or totalProducts = totalProductsCart
            list.add(0, CartUiModel.Subtotal(subtotal = subtotal, totalProducts = totalProductsCart))
            list.add(
                    CartUiModel.SummaryItem(
                        address = authStore.getAddress(),
                        deliveredTypeOne = authStore.getDeliveredType(),
                        currentTimeDelivered = Date().toString(),
                        email = authStore.getEmail() ?: "Ponga su e-mail",
                        phone = authStore.getPhone()
                            ?: "Ponga su número de movil para comprar como invitado",
                        typeOne = _types.value,
                        payment = _payment.value,
                        costSd =  _costSd.value?:0.0,
                        costEco = _costEcommerce.value?.toDouble()?:0.0,
                        totalCost = _costDeliver.value?.toDouble()?:0.0
                    )
                )

        println("currentTimeDelivered Date::: ${Date()}")

        _items.value = list
    }

    fun setQuantity(
        quantity: Int,
        id: Int,
    ) {
        try {
            authStore.setQuantityCartItem(
                quantity = quantity,
                id = id
            )
        } catch (e: Exception) {
            _messageEvent.value = Event(e.message)
        }
        loadCart()
    }

    fun setQuantityRef(
        quantity: Int,
        id: Int,
        ref: String,
        stock: Int,
        type: String
    ) {
        try {
            authStore.setQuantityCartItemRef(
                quantity =  quantity,
                id = id,
                ref = ref,
                stock = stock,
                type = type
            )

            loadCartToList()

        } catch (e: Exception) {
            _messageEvent.value = Event(e.message)
        }
        loadCart()
    }

    fun loadCartToList(): List<CartUiModel.ItemListCheckout>{
        val list: MutableList<CartUiModel.ItemListCheckout> = mutableListOf()
        val cartItems = authStore.getCart().map {
            //it.toCartUiModel()
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

    fun removeItem(id: Int, context: Context) {
        try {
            authStore.removeItemFromCart(id)
        } catch (e: Exception) {
            _messageEvent.value = Event(e.message)
            return
        }
        loadCart()
        _messageEvent.value = Event(context.getString(R.string.item_removed_from_cart))
    }
    fun removeItemRef(ref: String, type: String,context: Context) {
        try {
            authStore.removeItemFromCartRef(ref, type)
        } catch (e: Exception) {
            _messageEvent.value = Event(e.message)
            return
        }
        loadCart()
        //_messageEvent.value = Event(context.getString(R.string.item_removed_from_cart))
    }

    fun removeItemReference(reference: Int, context: Context) {
        try {
            authStore.removeItemFromCart(reference)
        } catch (e: Exception) {
            _messageEvent.value = Event(e.message)
            return
        }
        loadCart()
        _messageEvent.value = Event(context.getString(R.string.item_added_to_cart))
    }

    fun setDelivered(
        deliveredType: String,
    ) {
        deliveredType.let { authStore.setDeliveredType(it) }
    }


    fun setPayment(
        encryptedCard: String,
        encryptedDate: String,
        encryptedYear: String,
        encryptedSecurityCode: String,
        encryptedName: String
    ) {
        _payment.value = PaymentMethod(type = "scheme",encryptedCard,encryptedDate, encryptedYear, encryptedSecurityCode, encryptedName)
    }

    fun setCostSDValue(
        newCostSD: Double,

    ) {
       authStore.setCarriersSd(newCostSD.toString())
    }

    fun setCostEcoValue(
        newCostEco: Double,

        ) {
        authStore.setCarriersEco(newCostEco.toString())
    }

    fun setCurrentTimeDelivered(
        currentTimeDelivered: String,
    ) {
        currentTimeDelivered.let { authStore.setCurrentTimeDelivered(it) }
    }


    fun getType(): String {
        return authStore.getType() ?: ""
    }

    fun getCart(): List<CartUiModel.Item> {
        val cartItems = authStore.getCart().map {
            it.toCartUiModel()
        }
        return cartItems
    }

    fun isLoggedIn(): Boolean {
        return authStore.isLoggedIn()
    }

    fun getPublicKey(): String {
        return PUBLLIC_KEY
    }

    companion object {

        private val PUBLLIC_KEY =
            "10001|E56D388FC118357D813AC3B5FC1AB56E9E9924130D5490F81ECB3C4E4B148128CBB56D0C9265C826309C389DC804660D5A86CE19065230F3801A2ABD196E19B1F7F499B439AE525290EA73F7A65AAB89F7DC3D56A286647CC668DA6585A6F9ECFCBF7630C60551894F8309BB0E9A76E88ACC5199881058EB6F89C00BA9E5DC099ED96F0425853B3DD1032B461198F705C5291AE664E4FD2E151F81B85F8929C805387AE6608119E8C4741783377ABE1FF6F68A320C603E3723D657019E6623D0C020AB33D1F472C4E90198920ACFAE3BEE33CEE6F5FDA30B62F738B892C60A2A6B0F620FB29450869A889B578F21821628DABFC9263F2584C71421B04784DDA5"
        private val PAYMENT_METHOD_URL = "https://checkout-test.adyen.com/v66/paymentMethods"

        private val TAG = LogUtil.getTag()
        private val CONTENT_TYPE: MediaType = "application/json".toMediaType()
    }

    fun checkout(
        clientType: Boolean,
        itens: List<CartUiModel.ItemListCheckout>,
        costdelivered: Double,
        currentTime: String,
        deliveryType: String
    ) {
        val idguess = "000"

        try {
            viewModelScope.launch {
                val result = runCatching {
                    checkoutRepository.fetchCheckout(
                        clientType = clientType,
                        user = authStore.getUser() ?: UserModel(
                            userId = idguess.toLong(),
                            name = clientType.toString(),
                            email = authStore.getEmail()?: authStore.getUser()?.email.toString(),
                            sex = authStore.getUser()?.sex?:0,
                            birthdate = authStore.getUser()?.birthdate?:"",
                            phone = authStore.getUser()?.phone,
                            surname = authStore.getUser()?.surname
                        ),
                        address = authStore.getAddress()?: Address("Calle test",
                            "Number 320 test",0.0,0.1,
                            "State test","08054",
                             "City test","Region test",
                            "932700000","CountryId",
                            "CountryName","CountrCode test",
                            "CountryLang test"),
                        email = authStore.getUser()?.email?: authStore.getEmail().toString(),
                        newsLetter = true,
                        deliveryDate = currentTime,
                        deliveryRange = deliveryType,
                        items = itens,
                        shippingAddress = "",
                        customAddress = true,
                        digitalDelivery = "",
                        ecommerceShippingPrice = costdelivered
                    )
                }
                if (result != null) {

                    _checkoutResult.value =
                        CheckoutResult(success = "chekout Ok" ?: " -- ")

                    _refOrder.value = result.getOrThrow().ref_order
                    CartUiModel.RefOrderCheckout(refOrder = result.getOrThrow().ref_order)
                        .toString()

                    println("result =${result.getOrThrow().ref_order}")

                } else {
                    _checkoutResult.value = CheckoutResult(error = R.string.checkout_failed ?: 0)
                    println("error")
                }
            }
        } catch (e: Exception) {
            println("${e.message}  error---  ${e.printStackTrace()}")
        }
    }

    fun carriersCheckout(clientType: Boolean, itens: List<CartUiModel.ItemListCheckout>, retailID: String){
        println(" START Carriers CartViewModel line 303")

        try {
            viewModelScope.launch {
                val result = runCatching {
                    checkoutRepository.fetchCarriersCheckout(
                        requestRetailId = retailID,
                        clientType = clientType,
                        itens
                    )
                }

                println("  cost sameday:: ${result.getOrThrow().sd}")
                println("  cost ecommerce:: ${result.getOrThrow().ecommerce}")
                if (result != null) {

                    _checkoutResult.value =
                        CheckoutResult(success = "Checkout Ok")


                    _costSd.value = result.getOrThrow().sd
                    _costEcommerce.value = result.getOrThrow().ecommerce
                    _costDeliver.value = result.getOrThrow().sd.plus(result.getOrThrow().ecommerce)

                    authStore.setCarriersEco(result.getOrThrow().ecommerce.toString())
                    authStore.setCarriersSd(result.getOrThrow().sd.toString())
                    authStore.setCarriers(_costDeliver.value.toString())

                    loadCarriers()
                } else {
                    _checkoutResult.value = CheckoutResult(error = R.string.checkout_failed)
                    println("error")
                }
            }
            println(" END Carriers CartViewModel line 337")
        } catch (e: Exception) {
            println("${e.message}  -Error-  ${e.printStackTrace()}")
        }

    }

    fun carriersCheckoutCostSd(clientType: Boolean): Double {
        val items: List<CartUiModel.ItemListCheckout> = loadCartToList()
        var costSd = 0.0
        try {
            viewModelScope.launch {
                val result = runCatching {
                    checkoutRepository.fetchCarriersCheckout(
                        requestRetailId = authStore.getRetailID()?:"0",
                        clientType = clientType,
                        items
                    )
                }


                if (result != null) {

                    _checkoutResult.value =
                        CheckoutResult(success = "Chekout Ok")
                    costSd = result.getOrThrow().sd
                    _costSd.value = result.getOrThrow().sd
                    _costEcommerce.value = result.getOrThrow().ecommerce
                    _costDeliver.value = result.getOrThrow().sd.plus(result.getOrThrow().ecommerce)

                    authStore.setCarriersEco(result.getOrThrow().ecommerce.toString())
                    authStore.setCarriersSd(result.getOrThrow().sd.toString())
                    authStore.setCarriers(_costDeliver.value.toString())

                    loadCarriers()
                } else {
                    _checkoutResult.value = CheckoutResult(error = R.string.checkout_failed)
                }
            }
        } catch (e: Exception) {
            println("${e.message}  -Error-  ${e.printStackTrace()}")
        }
        return costSd
    }

    fun carriersCostSd(clientType: Boolean, items: List<CartUiModel.ItemListCheckout>): Double {
        var costSd = 0.0
        try {
            viewModelScope.launch {
                val result = runCatching {
                    checkoutRepository.fetchCarriersCheckout(
                        requestRetailId = authStore.getRetailID()?:"0",
                        clientType = clientType,
                        items
                    )
                }


                if (result != null) {

                    _checkoutResult.value =
                        CheckoutResult(success = "Chekout Ok")
                    costSd = result.getOrThrow().sd
                    _costSd.value = result.getOrThrow().sd
                    _costEcommerce.value = result.getOrThrow().ecommerce
                    _costDeliver.value = result.getOrThrow().sd.plus(result.getOrThrow().ecommerce)

                    authStore.setCarriersEco(result.getOrThrow().ecommerce.toString())
                    authStore.setCarriersSd(result.getOrThrow().sd.toString())
                    authStore.setCarriers(_costDeliver.value.toString())

                    println(" new::: ${result.getOrThrow().ecommerce}")
                    println(" new two::: ${result.getOrThrow().sd}")
                    println(" costSd::: $costSd")

                    loadCarriers()
                } else {
                    _checkoutResult.value = CheckoutResult(error = R.string.checkout_failed)
                }
            }
        } catch (e: Exception) {
            println("${e.message}  -Error-  ${e.printStackTrace()}")
        }
        return costSd
    }

    fun carriersCheckoutCostEco(clientType: Boolean): Double {

        val itens: List<CartUiModel.ItemListCheckout> = loadCartToList()

        var costEco = 0.0
        try {
            viewModelScope.launch {
                val result = runCatching {
                    checkoutRepository.fetchCarriersCheckout(
                        requestRetailId = authStore.getRetailID()?:"0",
                        clientType = clientType,
                        itens
                    )
                }


                if (result != null) {

                    _checkoutResult.value =
                        CheckoutResult(success = "Chekout Ok")
                    costEco = result.getOrThrow().ecommerce
                    _costSd.value = result.getOrThrow().sd
                    _costEcommerce.value = result.getOrThrow().ecommerce
                    _costDeliver.value = result.getOrThrow().sd.plus(result.getOrThrow().ecommerce)

                    authStore.setCarriersEco(result.getOrThrow().ecommerce.toString())
                    authStore.setCarriersSd(result.getOrThrow().sd.toString())
                    authStore.setCarriers(_costDeliver.value.toString())

                    loadCarriers()
                } else {
                    _checkoutResult.value = CheckoutResult(error = R.string.checkout_failed)
                    println("error")
                }
            }
        } catch (e: Exception) {
            println("${e.message}  -Error-  ${e.printStackTrace()}")
        }
        return costEco
    }

    fun carriersCheckoutTotal(clientType: Boolean, itens: List<CartUiModel.ItemListCheckout>): Double {
        var costTotal = 0.0
        try {
            viewModelScope.launch {
                val result = runCatching {
                    checkoutRepository.fetchCarriersCheckout(
                        requestRetailId = authStore.getRetailID()?:"0",
                        clientType = clientType,
                        itens
                    )
                }


                if (result != null) {

                    _checkoutResult.value =
                        CheckoutResult(success = "Chekout Ok")
                    costTotal = result.getOrThrow().sd.plus(result.getOrThrow().ecommerce)
                    _costSd.value = result.getOrThrow().sd
                    _costEcommerce.value = result.getOrThrow().ecommerce
                    _costDeliver.value = result.getOrThrow().sd.plus(result.getOrThrow().ecommerce)

                    authStore.setCarriersEco(result.getOrThrow().ecommerce.toString())
                    authStore.setCarriersSd(result.getOrThrow().sd.toString())
                    authStore.setCarriers(_costDeliver.value.toString())

                    loadCarriers()
                    loadCarriersSd()
                    loadCarriersEco()
                } else {
                    _checkoutResult.value = CheckoutResult(error = R.string.checkout_failed)
                    println("error")
                }
            }
        } catch (e: Exception) {
            println("${e.message}  -Error-  ${e.printStackTrace()}")
        }
        return costTotal
    }

    fun setCarriersSd(
        oldCarrierSd: String,
    ) {
        oldCarrierSd.let { authStore.setCarriersSd(it) }
    }

    fun setCarriersEco(
        oldCarrierEco: String,
    ) {
        oldCarrierEco.let { authStore.setCarriersEco(it) }
    }

    fun setCarriers(
        totalCarriers: String,
    ) {
        totalCarriers.let { authStore.setCarriers(it) }
    }


    fun loadCarriers(): Double {
        _costDeliver.value = (_costSd.value?.plus(_costEcommerce.value!!))
        return _costDeliver.value!!
    }

    private fun loadCarriersSd(): Double {
        return _costSd.value!!
    }
    private fun loadCarriersEco(): Double {
        return _costEcommerce.value!!
    }

    fun fetchAdyenPaymentMethodCheckoutTest(paymentMethods: PaymenthMethodsAdyen) {
        viewModelScope.launch {
            val result = runCatching {
                checkoutRepository.fetchAdyenPaymentMethodCheckoutTest(
                    paymentMethods = paymentMethods
                )
            }

            if (result != null) {

                _checkoutResult.value =
                    CheckoutResult(success = "chekout Ok" ?: " -- ")

               /** println(" result s: ${result.isSuccess}")
                println(" result f: ${result.isFailure}")
                println(" result exceptionOrNull: ${result.exceptionOrNull()}")
                println("  paymentMethods:: ${result.getOrThrow().name}")**/
            } else {
                _checkoutResult.value = CheckoutResult(error = R.string.checkout_failed ?: 0)
            }
        }
    }


    fun fetchPaymentTest(paymentRequest: PaymentRequestAdyen) {
        viewModelScope.launch {
            val result = runCatching {
                checkoutRepository.fetchPaymentsAdyenTest(
                    paymentRequest = paymentRequest
                )
            }

            if (result.isSuccess) {
                // Convert raw JSON to pretty JSON using GSON library
                val gson = GsonBuilder().setPrettyPrinting().create()
                val prettyJson = gson.toJson(
                        result.getOrThrow()
                            ?.toString()// About this thread blocking annotation : https://github.com/square/retrofit/issues/3255
                )
                Log.d("Pretty Printed JSON Payment Test:", prettyJson)
                _checkoutResult.value = CheckoutResult(result.getOrThrow().resultCode)

            } else {
                Log.e("RETROFIT_ERROR", result.toString())
            }


        }
    }
    fun getTotalItens(): Int{
        return authStore.getTotalCartItens()
    }
    fun fetchPaymentT(paymentRequest: PaymentRequestAdyen) {
        viewModelScope.launch {
            val result = runCatching {
                checkoutRepository.fetchPaymentsAdyenTest(
                    paymentRequest = paymentRequest
                )
            }

            if (result.isSuccess) {

                _checkoutResult.value =
                    CheckoutResult(success = "chekout Ok" ?: " -- ")
                println(" result s: ${result.isSuccess}")
                println(" result f: ${result.isFailure}")
                println(" result exceptionOrNull: ${result.exceptionOrNull()}")
                //println(" result notify(): ${result.notify()}")
                println(" resultCode:: ${result.getOrThrow().resultCode}")
            } else {
                _checkoutResult.value = CheckoutResult(error = R.string.checkout_failed ?: 0)
            }
        }
    }


    fun fetchPaymentPro(paymentRequest: PaymentRequestAdyen) {
        viewModelScope.launch {
            val result = runCatching {
                checkoutRepository.fetchPaymentAdyenPro(
                    paymentRequest = paymentRequest
                )
            }

            if (result.isSuccess) {
                // Convert raw JSON to pretty JSON using GSON library
                val gson = GsonBuilder().setPrettyPrinting().create()
                val prettyJson = gson.toJson(
                    //JsonParser.parseString(
                    result.getOrThrow()
                        ?.toString()// About this thread blocking annotation : https://github.com/square/retrofit/issues/3255
                    // )
                )
                Log.d("Pretty Printed JSON::", prettyJson)
            } else {
                Log.e("RETROFIT_ERROR", result.toString())
            }



           /** if (result != null) {

                _checkoutResult.value =
                    CheckoutResult(success = "chekout Ok" ?: " -- ")
                println(" result s: ${result.isSuccess}")
                println(" result f: ${result.isFailure}")
                println(" result exceptionOrNull: ${result.exceptionOrNull()}")
                println(" resultCode:: ${result.getOrThrow().resultCode}")
            } else {
                _checkoutResult.value = CheckoutResult(error = R.string.checkout_failed ?: 0)
            }**/
        }
    }

    fun loadCheckout(): MutableList<CartUiModel.ItemListCheckout>{

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

        list.map {
            println(" it list items cart:  $it")

        }
        return list
    }

}