package com.miscota.android.addressold

import android.Manifest
import android.content.Context
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.miscota.android.R
import com.miscota.android.address.PlacesAutoCompleteAdapter
import com.miscota.android.databinding.ActivityAddressOldBinding
import com.miscota.android.databinding.PartialLayoutRecentAddressBinding
import com.miscota.android.databinding.PartialLayoutRecentAddressOldBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.io.IOException
import java.util.*


class AddressActivityOld : AppCompatActivity() {

    private lateinit var binding: ActivityAddressOldBinding

    private val viewModel by viewModel<AddressViewModelOld>()

    private var _map: GoogleMap? = null

    private lateinit var geoCoder: Geocoder

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    var jobSearchLocation: Job? = null

    private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (!permissions.any { !it.value }) {
                setCurrentLocation()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityAddressOldBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
        geoCoder = Geocoder(this, Locale.getDefault())

        binding.toolbar.imageBack.setOnClickListener {
            finish()
        }

        initMap()

        setupAutoCompleteSearch()


        viewModel.selectedPlaceAddress.observe(this) {
            binding.placeLocation.text = it.address
            binding.placeLocationText.text = it.address

            println("it.addressss ${it.address}")

            _map?.clear()

            val position = LatLng(it.lat, it.lng)

            val markerOption = MarkerOptions()
                .position(position)
            _map?.addMarker(markerOption)

            // todo : I commented this lines due to no map key is provided CameraUpdateFactory need map should be initialized first
//            val cameraPosition =
//                CameraPosition.Builder().target(position).zoom(DEFAULT_MAP_ZOOM).build()
//            val cameraUpdateFactory = CameraUpdateFactory.newCameraPosition(cameraPosition)
//            _map?.animateCamera(cameraUpdateFactory)
        }

        viewModel.recentAddresses.observe(this) {
            it.forEach { address ->
                //val partialBinding = PartialLayoutRecentAddressBinding.inflate(layoutInflater)
                val partialBinding = PartialLayoutRecentAddressOldBinding.inflate(layoutInflater)
                binding.recentAddressLayout.addView(partialBinding.root)

                //ialBinding.currentLocation.text = address.address
                partialBinding.currentLocation.text = address.address
                println(" address.address:: ${address.address}")
                partialBinding.currentLocationText.text = "${address.postalCode}, ${address.city}, ${address.countryName}"

                partialBinding.root.setOnClickListener {
                    val additionalAddress = binding.additionalAddress.text.toString()
                    viewModel.setAddress(additionalAddress, address)
                    viewModel.setAddressInfo(additionalAddress, address)
                }
            }
        }

        viewModel.navigateBackEvent.observe(this) {
            finish()
        }

        binding.currentLocationLayout.setOnClickListener {
            val additionalAddress = binding.additionalAddress.text.toString()
            viewModel.setCurrentAddress(additionalAddress)
        }

        binding.placesLocationLayout.setOnClickListener {
            val additionalAddress = binding.additionalAddress.text.toString()
            viewModel.setSelectedPlaceAddress(additionalAddress)
        }
    }

    private fun initMap() {
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync { map ->
            _map = map

            _map?.setOnMapClickListener { latLng ->
                val listOfAddress = geoCoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                println("listOfAddress $listOfAddress")
                listOfAddress.forEach { address ->
                    viewModel.setPlaceFromGeocode(address)
                }
            }
        }

        viewModel.currentAddress.observe(this) { data ->
            binding.currentLocation.text = data.address
            binding.currentLocationText.text = data.address
            binding.placeLocation.text = data.address
            binding.placeLocationText.text = data.address

            _map?.clear()

            val position = LatLng(data.lat, data.lng)

            val markerOption = MarkerOptions()
                .position(position)
            _map?.addMarker(markerOption)

            // todo : I commented this lines due to no map key is provided CameraUpdateFactory need map should be initialized first
//            val cameraPosition =
//                CameraPosition.Builder().target(position).zoom(DEFAULT_MAP_ZOOM).build()
//            val cameraUpdateFactory = CameraUpdateFactory.newCameraPosition(cameraPosition)
//            _map?.animateCamera(cameraUpdateFactory)
        }
    }

    fun isConnected(): Boolean {
        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

    private fun setupAutoCompleteSearch() {
        val adapter = PlacesAutoCompleteAdapterOld(this)


        binding.addressAutoComplete.setAdapter(adapter)

        binding.addressAutoComplete.doAfterTextChanged {
            jobSearchLocation?.cancel()
            if (!it.isNullOrEmpty()) {
                jobSearchLocation = lifecycleScope.launchWhenResumed {

                    try {

                        delay(500L)
                        val listOfAddress =
                            geoCoder.getFromLocationName(it.toString(), 10)
                        println(" listOfAddress.listOfAddress $listOfAddress")
                        viewModel.loadPlacesSuggestions(listOfAddress)

                    }catch (e: IOException){

                        println(" Error: ${e.message} captured")
                        if (isConnected()) {
                            println(" Internet Connected with another Error: ${e.message}")

                        }
                        else{

                            println(" Error: ${e.message} because Internet Disconnected")
                            Toast.makeText(this@AddressActivityOld, R.string.message_conected, Toast.LENGTH_LONG).show()
                        }

                    }
                }
            }
        }

        binding.addressAutoComplete.setOnItemClickListener { _, _, _, itemId ->
            binding.addressAutoComplete.clearFocus()
            viewModel.loadPlaceFromId(itemId = itemId)
        }

        viewModel.placesSuggestion.observe(this) {
            adapter.clear()
            adapter.addAll(it)
            adapter.notifyDataSetChanged()
        }
    }

    private fun setCurrentLocation() {
        lifecycleScope.launchWhenResumed {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        val latLng = LatLng(location.latitude, location.longitude)

                        val listOfAddress =
                            geoCoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

                        listOfAddress.forEach { address ->
                            viewModel.setCurrentLocationFromGeocode(address)
                        }
                    }
                }
            } catch (e: SecurityException) {
                Timber.e("Location permission not granted")
            }
        }
    }

    companion object {
        private const val DEFAULT_MAP_ZOOM = 17f
    }

}