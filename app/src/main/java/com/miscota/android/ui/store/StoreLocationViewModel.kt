package com.miscota.android.ui.store

import android.app.Application
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.provider.Settings
import androidx.lifecycle.*
import com.facebook.FacebookSdk
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.miscota.android.repository.StoreLocationRepository
import com.miscota.android.util.AuthStore
import com.miscota.android.util.Event
import kotlinx.coroutines.*
import java.util.*


class StoreLocationViewModel(
        val app: Application,
        val storeLocationRepository: StoreLocationRepository,
        val authStore: AuthStore,
) : AndroidViewModel(app) {

    private val _currentLocation: MutableLiveData<LatLng> = MutableLiveData()
    val currentLocation: LiveData<LatLng> = _currentLocation

    private val _dataList: MutableLiveData<List<StoreLocationUiModel>> = MutableLiveData()
    val dataList: LiveData<List<StoreLocationUiModel>> = _dataList

    private val _messageEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val messageEvent: LiveData<Event<String>> = _messageEvent

    var spinnerLoading = MutableLiveData<Boolean>()

    var job: Job? = null

    fun loadStores() {
        spinnerLoading.value = true
        job?.cancel()
        _dataList.value = listOf()
        loadData()
    }

    fun geoCodeInput(input: String): Location? {

        if (input.isEmpty())
            return null

        val geocoder = Geocoder(app)

        val addresses = geocoder.getFromLocationName(input, 1)

        if (addresses.size == 1) {
            return Location(LocationManager.GPS_PROVIDER).apply {
                latitude = addresses[0].latitude
                longitude = addresses[0].longitude
            }
        }
        return null
    }

    private fun loadData() {
        viewModelScope.launch {
            val result = runCatching {
                val response = storeLocationRepository.getRetailers()

                val geocoder = Geocoder(app)

                val list = withContext(Dispatchers.IO) {
                    response.mapNotNull {
                        it.toStoreLocationUiModel(geocoder)
                    }
                }


                currentLocation.value?.let { location ->
                    val current = Location(LocationManager.GPS_PROVIDER).apply {
                        latitude = location.latitude
                        longitude = location.longitude
                    }


                    Collections.sort(list) { store1, store2 ->
                        val storeLocation1 = Location(LocationManager.GPS_PROVIDER).apply {
                                latitude = store1.latitude
                                longitude = store1.longitude
                        }

                        val storeLocation2 = Location(LocationManager.GPS_PROVIDER).apply {
                            latitude = store2.latitude
                            longitude = store2.longitude
                        }

                        (current.distanceTo(storeLocation1) - current.distanceTo(storeLocation2)).toInt()
                    }
                    _dataList.value = list
                } ?: run {
                    _dataList.value = list
                }
            }

            spinnerLoading.value = false

            val exception = result.exceptionOrNull()
            if (exception != null && exception !is CancellationException) {
                _messageEvent.value = Event(exception.message.toString())
            }
        }
    }

    fun setItemSelected(selectedItem: StoreLocationUiModel) {
        val list = _dataList.value ?: listOf()
        val newList = list.map { item ->
            if (item.id == selectedItem.id) {
                return@map item.copy(isSelected = true)
            }
            return@map item.copy(isSelected = false)
        }
        _dataList.value = newList
    }

    fun setCurrentLocation(position: LatLng) {
        _currentLocation.value = position
    }

    fun triggerCurrentLocation() {
        //val crashlytics = FirebaseCrashlytics.getInstance()
        //crashlytics.checkForUnsentReports()

        if( _currentLocation.value != null) {
            _currentLocation.value = _currentLocation.value
        }
    }

    fun checkIfLocationOpened(): Boolean {
        val provider: String = Settings.Secure.getString(
            FacebookSdk.getApplicationContext().contentResolver,
            Settings.Secure.LOCATION_PROVIDERS_ALLOWED
        )
        println("Provider contains=> $provider")
        return provider.contains("gps") || provider.contains("network")
    }

}