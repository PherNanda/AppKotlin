package com.miscota.android.address

import android.location.Location
import android.location.LocationManager
import android.provider.Settings
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.FacebookSdk
import com.google.android.gms.maps.model.LatLng
import com.miscota.android.repository.StoreLocationRepository
import com.miscota.android.ui.store.Store
import com.miscota.android.util.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class AddressViewModel(
    val authStore: AuthStore,
    private val placesRepository: PlacesRepository,
    private val storeLocationRepository: StoreLocationRepository
) : ViewModel() {

    private val _currentLocation: MutableLiveData<LatLng> = MutableLiveData()
    val currentLocation: LiveData<LatLng> = _currentLocation

    private val _recentAddresses: MutableLiveData<List<Address>> = MutableLiveData()
    val recentAddresses: LiveData<List<Address>> = _recentAddresses

    private val _recentAddressesUser: MutableLiveData<List<Address>> = MutableLiveData()
    val recentAddressesUser: LiveData<List<Address>> = _recentAddressesUser

    private val _currentAddress: MutableLiveData<Address> = MutableLiveData()
    val currentAddress: LiveData<Address> = _currentAddress

    private val _selectedPlaceAddress: MutableLiveData<Address> = MutableLiveData()
    val selectedPlaceAddress: LiveData<Address> = _selectedPlaceAddress

    private val _placesSuggestion = MutableLiveData<List<PlaceSuggestionUiModel>>(listOf())
    val placesSuggestion: LiveData<List<PlaceSuggestionUiModel>> = _placesSuggestion

    private val _navigateBackEvent: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val navigateBackEvent: LiveData<Event<Boolean>> = _navigateBackEvent

    private var _requestID: MutableLiveData<Store> = MutableLiveData()
    val requestID: LiveData<Store> = _requestID

    private val isSameDayEnabled = MutableLiveData<Boolean>()

    private var _addressChanged = MutableLiveData(false)
    val addressChanged: LiveData<Boolean> = _addressChanged

    private var job: Job? = null

    init {
        loadRecentAddresses()
        loadAddressesUser()

        _recentAddressesUser.value?.let { _recentAddresses.value?.let { it1 ->
            checkChangeAddress(
                it1, it)
        } }
    }

    private fun loadRecentAddresses() {
        _recentAddresses.value = authStore.getRecentAddresses() ?: listOf()
    }

    private fun loadAddressesUser() {
        _recentAddressesUser.value = authStore.getRecentAddressesInfo() ?: listOf()
    }

    fun loadPlacesSuggestions(addresses: List<android.location.Address>, postalCode: String) {
        _placesSuggestion.value = addresses.map { address ->
            val state = GeocoderState.toShortForm(address.adminArea ?: "")
            val model = Address(
                address = address.getAddressLine(0)?.split(",")?.firstOrNull() ?: "",
                addressNumber = address.getAddressLine(0)?.split(",")?.firstOrNull() ?: "",
                postalCode = address.postalCode ?: postalCode,
                state = state,
                city = address.locality ?: address.getAddressLine(0)?.split(",")?.firstOrNull().toString(),
                lng = address.longitude,
                lat = address.latitude,
                region = state,
                phone = authStore.getPhone(),
                countryId = address.countryCode ?:"",
                countryCode = address.countryCode ?: "",
                countrylang = authStore.getAddress()?.countrylang ?: "",
                countryName = address.countryName ?: ""

            )

            return@map PlaceSuggestionUiModel(
                id = (address.longitude + address.latitude).toLong(),
                placeId = "",
                addressText = address.getAddressLine(0)?.split(",")?.firstOrNull() ?: "",
                address = model,
            )
        }
    }

    fun loadPlaceFromId(itemId: Long) {
        val place =
            _placesSuggestion.value?.findLast { (it.address.lng + it.address.lat).toLong() == itemId }

        _selectedPlaceAddress.value = place?.address
    }

    fun setPlaceFromGeocode(address: android.location.Address) {
        val state = GeocoderState.toShortForm(address.adminArea ?: "")
        val model = Address(
            address = address.getAddressLine(0)?.split(",")?.firstOrNull() ?: "",
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
        _selectedPlaceAddress.value = model
    }

    fun setPlacesLocationFromGeocode(address: android.location.Address) {
        val state = GeocoderState.toShortForm(address.adminArea ?: "")
        val model = Address(
            address = address.getAddressLine(0)?.split(",")?.firstOrNull() ?: "",
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
        _selectedPlaceAddress.value = model
    }

    fun setCurrentLocationFromGeocode(address: android.location.Address) {
        val state = GeocoderState.toShortForm(address.adminArea ?: "")
        val model = Address(
            address = address.getAddressLine(0)?.split(",")?.firstOrNull() ?: "",
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
        _currentAddress.value = model
    }

    fun setAddress(additionalAddress: String, address: Address?) {
        val newAddress = address?.copy(address = "$additionalAddress ${address.address}")

        authStore.setAddress(newAddress)
        authStore.addRecentAddress(newAddress)
        authStore.addAddressUser(authStore.getAddressInfo())
        authStore.addRecentAddressInfo(authStore.getAddressInfo())
        newAddress?.postalCode?.let { checkPostalCode(it) }

        _navigateBackEvent.value = Event(true)
    }


    fun setAddressTwo(additionalAddress: String, address: Address?) {
        val newAddress = address?.copy(address = "$additionalAddress ${address.address}")

        authStore.addRecentAddress(newAddress)
        //authStore.addRecentAddress(newAddress)
        //authStore.addAddressUser(authStore.getAddressInfo())
        //authStore.addRecentAddressInfo(authStore.getAddressInfo())

    }


    fun goToMain(address: Address?) {

        authStore.setAddress(address)
        authStore.addRecentAddress(address)
        authStore.addAddressUser(address)
        //authStore.addRecentAddressInfo(address)

        _navigateBackEvent.value = Event(true)
    }

    fun setAdressUser(address: Address?) {

        authStore.setAddress(address)
        _navigateBackEvent.value = Event(true)
    }

    fun setAddressInfo(additionalAddress: String, address: Address?) {
        val newAddress = address?.copy(address = "$additionalAddress ${address.address}")

        authStore.setAddressInfo(newAddress)
        authStore.addRecentAddressInfo(newAddress)

        _navigateBackEvent.value = Event(true)
    }
    fun isLoggedIn(): Boolean {
        return authStore.isLoggedIn()
    }

    fun setSelectedPlaceAddress(additionalAddress: String) {
        _selectedPlaceAddress.value?.let {
            setAddress(additionalAddress, it)
           // setAddressInfo(additionalAddress,it)
        }
    }

    fun setCurrentAddress(additionalAddress: String) {
        _currentAddress.value?.let {
            setAddress(additionalAddress, it)
        }
    }

    fun setCurrentLocation(position: LatLng) {
        _currentLocation.value = position
    }

    fun triggerCurrentLocation() {
        if( _currentLocation.value != null) {
            _currentLocation.value = _currentLocation.value
        }
    }

    fun checkIfLocationOpened(): Boolean {
        val provider: String = Settings.Secure.getString(
            FacebookSdk.getApplicationContext().contentResolver,
            Settings.Secure.LOCATION_PROVIDERS_ALLOWED
        )
        return provider.contains("gps") || provider.contains("network")
    }

    fun loadStores() {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val result = runCatching {

                currentLocation.value?.let { location ->
                    val current = Location(LocationManager.GPS_PROVIDER).apply {
                        latitude = location.latitude
                        longitude = location.longitude
                    }
                }
            }
        }
    }

    fun checkPostalCode(postalCode: String) {

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

                    llist.map {

                        _requestID.value = it
                        println(" List Retail AddressViewModel  ${it.name} -- ${it.retail_shop_id}")
                    }
                    //isSameDayEnabled.value = true
                    authStore.setRetailID(_requestID.value?.retail_shop_id)
                    return@launch
                }
                if (_requestID.value == null){
                    val retailIdDefault = "0"
                    _requestID.value = Store("Retail_ecommerce",retailIdDefault)
                    authStore.setRetailID(retailIdDefault)

                }
                if(response.isEmpty()){
                    val retailIdDefault = "0"
                    _requestID.value = Store("Retail_ecommerce",retailIdDefault)
                    authStore.setRetailID(retailIdDefault)

                }
            }
        }

        isSameDayEnabled.value = false
    }

    fun checkChangeAddress(addresses: List<Address>, moreAddresses: List<Address>): Boolean{
        _addressChanged.value = addresses.isNotEmpty() || moreAddresses.isNotEmpty()
        return _addressChanged.value!!
    }


    companion object {
        const val SEARCH_DELAY = 300L
    }
}