package com.miscota.android.addressold

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.miscota.android.util.*
import kotlinx.coroutines.Job
import java.util.*

class AddressViewModelOld(
    private val authStore: AuthStore,
    private val placesRepository: PlacesRepositoryOld,
) : ViewModel() {

    private val _recentAddresses: MutableLiveData<List<Address>> = MutableLiveData()
    val recentAddresses: LiveData<List<Address>> = _recentAddresses

    private val _currentAddress: MutableLiveData<Address> = MutableLiveData()
    val currentAddress: LiveData<Address> = _currentAddress

    private val _selectedPlaceAddress: MutableLiveData<Address> = MutableLiveData()
    val selectedPlaceAddress: LiveData<Address> = _selectedPlaceAddress

    private val _placesSuggestion = MutableLiveData<List<PlaceSuggestionUiModelOld>>(listOf())
    val placesSuggestion: LiveData<List<PlaceSuggestionUiModelOld>> = _placesSuggestion

    private val _navigateBackEvent: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val navigateBackEvent: LiveData<Event<Boolean>> = _navigateBackEvent

    private var job: Job? = null

    init {
        loadRecentAddresses()
    }

    private fun loadRecentAddresses() {
        _recentAddresses.value = authStore.getRecentAddresses() ?: listOf()
    }

    fun loadPlacesSuggestions(addresses: List<android.location.Address>) {
        _placesSuggestion.value = addresses.map { address ->
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
            return@map PlaceSuggestionUiModelOld(
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
            setAddressInfo(additionalAddress,it)
        }
    }

    fun setCurrentAddress(additionalAddress: String) {
        _currentAddress.value?.let {
            setAddress(additionalAddress, it)
        }
    }

    companion object {
        const val SEARCH_DELAY = 300L
    }
}