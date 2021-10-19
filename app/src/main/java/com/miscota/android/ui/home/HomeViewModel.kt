package com.miscota.android.ui.home

import android.provider.Settings
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.FacebookSdk.getApplicationContext
import com.miscota.android.R
import com.miscota.android.api.autoship.response.AutoShipResponse
import com.miscota.android.repository.AuthRepository
import com.miscota.android.repository.AutoShipRepository
import com.miscota.android.repository.StoreLocationRepository
import com.miscota.android.ui.store.Store
import com.miscota.android.util.Address
import com.miscota.android.util.AuthStore
import com.miscota.android.util.Event
import com.miscota.android.util.GeocoderState
import io.card.payment.i18n.LocalizedStrings.getString
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel(private val authRepository: AuthRepository,
                    private val authStore: AuthStore,
                    private val autoShipRespository: AutoShipRepository,
                    private val storeLocationRepository: StoreLocationRepository,) :
    ViewModel() {

    private val _showAutoShip: MutableLiveData<Event<AutoShipResponse>> = MutableLiveData()
    val showAutoShip: LiveData<Event<AutoShipResponse>> = _showAutoShip

    private val _selectedLocation: MutableLiveData<Address> =
        MutableLiveData(authStore.getAddress())
    val selectedLocation: LiveData<Address> = _selectedLocation

    private val _costEcommerce = MutableLiveData(0.0)
    val costEcommerce: LiveData<Double> = _costEcommerce

    private val _costSd = MutableLiveData(0.0)
    val costSd: LiveData<Double> = _costSd

    private var _requestID: MutableLiveData<Store> = MutableLiveData()
    val requestID: LiveData<Store> = _requestID

    val isSameDayEnabled = MutableLiveData<Boolean>()


    init {
        loadAutoShipData()
        _costSd.value = authStore.getCarriersSd()?.toDouble()
        _costEcommerce.value = authStore.getCarriersEco()?.toDouble()
    }

    fun isLoggedIn(): Boolean {
        return authStore.isLoggedIn()
    }

    private fun loadAutoShipData() {
        val userId = authStore.getUser()?.userId
        if (userId != null) {
            viewModelScope.launch {
                val result = runCatching {
                    autoShipRespository.fetchAutoShipData(
                        userId = userId.toInt()
                    )
                }

                if (result.isSuccess) {
                    val autoShipData = result.getOrThrow()
                    if (autoShipData.any()) {
                        _showAutoShip.value = Event(autoShipData.first())
                    } else {
                        _showAutoShip.value = Event(null)
                    }
                } else {
                    Timber.d(result.exceptionOrNull())
                }
            }
        }
    }

    fun getTotalItens(): Int{
        return authStore.getTotalCartItens()
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

    /**fun checkPostalCode(postalCode: String) {
        /**if (postalCode == "25199" || postalCode == "94043") {
        isSameDayEnabled.value = true
        return
        }**/
        println(" postalCode: $postalCode")
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
                        println(" List Retail   ${it.name} -- ${it.retail_shop_id}")
                    }

                    authStore.setRetailID(_requestID.value?.retail_shop_id)
                    return@launch
                }
            }
        }

        isSameDayEnabled.value = false
    }**/

    fun checkPostalCode(postalCode: String) {
        /**if (postalCode == "25199" || postalCode == "94043") {
        isSameDayEnabled.value = true
        return
        }**/
        println(" postalCode HomeViewModel: $postalCode")
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
                        println(" List Retail HomeViewModel  ${it.name} -- ${it.retail_shop_id}")
                    }

                    authStore.setRetailID(_requestID.value?.retail_shop_id)
                    return@launch
                }
                if (_requestID.value == null){
                    val retailIdDefault = "0"
                    _requestID.value = Store("Retail_ecommerce",retailIdDefault)
                    authStore.setRetailID(retailIdDefault)
                    println("HomeViewModel _requestID.value?.retail_shop_id line 179  ${_requestID.value?.retail_shop_id} -- ")
                    println("HomeViewModel retailIdDefault line 180  $retailIdDefault -- ")
                }
                if(response.isEmpty()){
                    val retailIdDefault = "0"
                    _requestID.value = Store("Retail_ecommerce",retailIdDefault)
                    authStore.setRetailID(retailIdDefault)
                    println("HomeViewModel _requestID.value?.retail_shop_id line 186  ${_requestID.value?.retail_shop_id} -- ")
                    println("HomeViewModel retailIdDefault line 187  $retailIdDefault -- ")
                }
            }
        }

        isSameDayEnabled.value = false
    }


    fun checkIfLocationOpened(): Boolean {
        val provider: String = Settings.Secure.getString(
            getApplicationContext().contentResolver,
            Settings.Secure.LOCATION_PROVIDERS_ALLOWED
        )
        println("Provider contains=> $provider")
        return provider.contains("gps") || provider.contains("network")
    }

    fun setType(
        type: String,
    ){
        type.let {
            authStore.setType(it)
        }
    }

    fun getType(): String?{
         return authStore.getType()
    }


}
