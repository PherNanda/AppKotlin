package com.miscota.android

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miscota.android.repository.CheckoutRepository
import com.miscota.android.repository.StoreLocationRepository
import com.miscota.android.ui.cart.CartUiModel
import com.miscota.android.ui.cart.toCartItemUiModel
import com.miscota.android.ui.cart.toCartUiModel
import com.miscota.android.ui.checkoutpayment.CheckoutResult
import com.miscota.android.ui.store.Store
import com.miscota.android.util.Address
import com.miscota.android.util.AuthStore
import com.miscota.android.util.Event
import com.miscota.android.util.GeocoderState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivityViewModel(
    val authStore: AuthStore,
    private val checkoutRepository: CheckoutRepository,
    private val storeLocationRepository: StoreLocationRepository
) : ViewModel() {

    private val _openLoginActivityEvent: MutableLiveData<Event<Boolean>> = MutableLiveData(Event(false))
    //private val _openLoginActivityEvent: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val openLoginActivityEvent: LiveData<Event<Boolean>> = _openLoginActivityEvent

    private val _checkoutResult = MutableLiveData<CheckoutResult>()
    val checkoutResult: LiveData<CheckoutResult> = _checkoutResult

    private val _costSd = MutableLiveData(0.0)
    val costSd: LiveData<Double> = _costSd

    private val _costEcommerce = MutableLiveData(0.0)
    val costEcommerce: LiveData<Double> = _costEcommerce

    private val _loguedIn: MutableLiveData<Boolean> = MutableLiveData()
    val loguedIn: LiveData<Boolean> = _loguedIn

    private val _selectedLocation: MutableLiveData<Address> =
        MutableLiveData(authStore.getAddress())
    val selectedLocation: LiveData<Address> = _selectedLocation

    private var _requestID: MutableLiveData<Store> = MutableLiveData()
    val requestID: LiveData<Store> = _requestID

    val isSameDayEnabled = MutableLiveData<Boolean>()

    private var _isAuthShow = MutableLiveData(0)
    val isAuthShow: LiveData<Int> = _isAuthShow


    fun loadSelectedLocation() {
        _selectedLocation.value = authStore.getAddress() ?: return
        println(" authStore.getAddressInfo() MainActivityViewModel ${authStore.getAddressInfo()}")
        println(" authStore.getAddressesUser()?.first() MainActivityViewModel ${authStore.getAddressesUser()?.first()}")
        println(" authStore.getAddress() MainActivityViewModel ${authStore.getAddress()}")
        println(" authStore.getRecentAddresses() MainActivityViewModel ${authStore.getRecentAddresses()}")
        println(" authStore.getRecentAddressesInfo() MainActivityViewModel ${authStore.getRecentAddressesInfo()}")
    }


    init {
        val currentDate = Date()
        val dateExpire = getDateLoginExpire()
        loadSelectedLocation()


        comproveLoginExpireDate(currentDate, dateExpire)

        if (!authStore.isLoggedIn() && isAuthShow.value == 0) {
            _openLoginActivityEvent.value = Event(true)
            _isAuthShow.value = 1
        }

        _loguedIn.value = authStore.isLoggedIn()

    }


    fun getTotalItens(): Int{
        return authStore.getTotalCartItens()
    }

    fun loadCheckout(): MutableList<CartUiModel.ItemListCheckout>{

        val list: MutableList<CartUiModel.ItemListCheckout> = mutableListOf()

        val cartItensCheckout = authStore.getCart().map {
            it.toCartItemUiModel()

            list.add(
                CartUiModel.ItemListCheckout(
                    qty = it.qty.toString(),
                    price = it.combinationPrice.toString(),
                    type = it.type,
                    ref = it.combinationReference,
                ))
        }

        list.map {
            println(" it list itens cartMain:  $it")
        }

        val listCartItem: MutableList<CartUiModel> = mutableListOf()
        val cartItems = authStore.getCart().map {
            it.toCartUiModel()
        }



        return list
    }

    fun getDateLoginExpire(): Date {
        var expireLoginDate: Date? = Date()

        println(" fecha autoLogin ${authStore.getAutoLoginParamExpire()}")

        if( !authStore.getAutoLoginParamExpire().isNullOrEmpty() )
        {
            val dateLoginExpire = SimpleDateFormat("yyyy-MM-DD hh:mm:ss").parse(authStore.getAutoLoginParamExpire()!!)
            //val dateLoginExpire = SimpleDateFormat("yyyy-MM-DD hh:mm:ss").parse("2021-11-11 11:15:59")

            expireLoginDate = dateLoginExpire
        }

        return expireLoginDate!!
    }

    fun comproveLoginExpireDate(currentDate: Date, dateExpire: Date){

        if ( currentDate.date <= dateExpire.date &&
            currentDate.month <= dateExpire.month &&
            currentDate.year <= dateExpire.year &&
            currentDate.hours <= dateExpire.hours &&
            currentDate.minutes <= dateExpire.minutes &&
            currentDate.seconds <= dateExpire.seconds )
        {

        }
        if ( currentDate.date > dateExpire.date &&
            currentDate.month >= dateExpire.month &&
            currentDate.year >= dateExpire.year )
        {
            authStore.setUser(null)
        }
        if ( currentDate.date == dateExpire.date &&
            currentDate.month == dateExpire.month &&
            currentDate.year == dateExpire.year &&
            ( currentDate.hours > dateExpire.hours ||
                    ( currentDate.hours == dateExpire.hours &&
                            currentDate.minutes > dateExpire.minutes) ))
        {
            authStore.setUser(null)
        }
    }

    fun loadAddress() {
        _selectedLocation.value = authStore.getAddress() ?: return
    }

    fun setCurrentLocationFromGeocode(address: android.location.Address) {
        val state = GeocoderState.toShortForm(address.adminArea ?: "")
        val model = Address(
            address = address.getAddressLine(0) ?: "",
            addressNumber = address.getAddressLine(0)?.split(",")?.firstOrNull() ?: "",
            postalCode = address.postalCode ?: "",
            state = state,
            city = address.locality ?: "",
            lng = address.longitude,
            lat = address.latitude,
            region = state,
            phone = authStore.getPhone(),
            countryId = address.countryCode ?:"",
            countryCode = address.countryCode ?: "",
            countrylang = authStore.getAddress()?.countrylang ?: "",
            countryName = address.countryName ?: ""
        )
        if (_selectedLocation.value == null) {
            _selectedLocation.value = model
        }
        authStore.setAddress(address = model)
    }

    fun checkPostalCode(postalCode: String) {
        /**if (postalCode == "25199" || postalCode == "94043") {
        isSameDayEnabled.value = true
        return
        }**/
        println(" postalCode MainActivityViewModel: $postalCode")
        viewModelScope.launch {
            val result = runCatching {
                val response = storeLocationRepository.getSameDayShops(postalCode)
                if (!response.isEmpty()) {
                    isSameDayEnabled.value = true

                    val llist: ArrayList<Store> = arrayListOf(
                        Store(
                            name= response.get(0).name,
                            retail_shop_id = response.get(0).retail_shop_id
                        )
                    )

                    llist.map { it

                        _requestID.value = it
                        println(" List Retail MainActivityViewModel  ${it.name} -- ${it.retail_shop_id}")
                    }
                    //isSameDayEnabled.value = true
                    authStore.setRetailID(_requestID.value?.retail_shop_id)
                    return@launch
                }
                if (_requestID.value == null){
                    val retailIdDefault = "0"
                    _requestID.value?.retail_shop_id == retailIdDefault
                    authStore.setRetailID(retailIdDefault)
                }
                if(response.isEmpty()){
                    val retailIdDefault = "0"
                    _requestID.value?.retail_shop_id == retailIdDefault
                    authStore.setRetailID(retailIdDefault)
                }
            }
        }

        isSameDayEnabled.value = false
    }

    fun setShowAuth(authShow: String?){
        authStore.setShowAuth(authShow?:null)
        println("isAuthShow.value::: MainActivityViewModel:: setShowAuth()  ${isAuthShow.value}")
        println("_isAuthShow.value::: MainActivityViewModel:: setShowAuth()  ${_isAuthShow.value}")
    }

    fun getShowAuth(): Boolean{
        return authStore.getShowAuth()
        println("isAuthShow authStore.getShowAuth()::: ${authStore.getShowAuth()}")
        println("isAuthShow.value::: MainActivityViewModel:: getShowAuth()  ${isAuthShow.value}")
        println("_isAuthShow.value::: MainActivityViewModel:: getShowAuth()  ${_isAuthShow.value}")
    }


}