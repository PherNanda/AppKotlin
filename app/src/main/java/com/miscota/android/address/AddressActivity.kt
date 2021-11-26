package com.miscota.android.address

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.miscota.android.R
import com.miscota.android.databinding.ActivityAddressBinding
import com.miscota.android.databinding.PartialLayoutRecentAddressBinding
import com.miscota.android.ui.cart.CartUiModel
import com.miscota.android.ui.cart.CartViewModel
import com.miscota.android.ui.cart.toCartItemUiModel
import com.miscota.android.ui.store.StoreLocationFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.io.IOException
import java.util.*


class AddressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddressBinding

    private val viewModel by viewModel<AddressViewModel>()

    private val viewModelCart by viewModel<CartViewModel>()

    private var _map: GoogleMap? = null

    private lateinit var geoCoder: Geocoder

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var lastLocation: Location

    private var jobSearchLocation: Job? = null

    var jobPlace: Job? = null

    private lateinit var listAddress:  MutableList<Address>

       private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (!permissions.any { !it.value }) {
                setCurrentLocationTwo()
            }
        }


    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            if (locationResult == null) {
                return
            }

            locationResult.locations.forEach { location ->
                val latLng = LatLng(location.latitude, location.longitude)

                viewModel.setCurrentLocation(latLng)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listAddress = arrayListOf()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        geoCoder = Geocoder(this, Locale.getDefault())

        binding.toolbar.imageBack.setOnClickListener {
            finish()
            //startActivity(Intent(this, MainActivity::class.java))
        }

        initMap()

        setupAutoCompleteSearch()

        binding.currentLocation.setOnClickListener {

            //fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ))

            //geoCoder = Geocoder(this, Locale.getDefault())
            if(!viewModel.checkIfLocationOpened()) {
                Toast.makeText(this,"Debes habilitar la localización",Toast.LENGTH_LONG).show()

           }else{
                viewModel.triggerCurrentLocation()

               println(" viewModel.currentLocation ${viewModel.currentLocation}" )

                if (listAddress.isNotEmpty() ) {
                    //binding.addressAutoComplete.text.append(listAddress.get(0).getAddressLine(0))
                    //binding.addressAutoComplete.text.append("${listAddress.get(0).postalCode}, ${listAddress.get(0).locality}, ${listAddress.get(0).countryName}")
                    //binding.addressAutoComplete.text.append("${listAddress.get(0).thoroughfare}, ${listAddress.get(0).featureName}, ${listAddress.get(0).postalCode}, ${listAddress.get(0).locality}")

                        /**binding.addressAutoComplete.text.append(
                        "${listAddress.get(0).thoroughfare}, ${
                            listAddress.get(
                                0
                            ).featureName
                        }, ${listAddress.get(0).postalCode}"
                    )**/
                    val address: com.miscota.android.util.Address =
                        com.miscota.android.util.Address(
                            address = listAddress[0].getAddressLine(0),
                            addressNumber = listAddress[0].getAddressLine(0),
                            lat = listAddress[0].latitude,
                            lng = listAddress[0].longitude,
                            state = listAddress[0].subAdminArea, //region
                            postalCode = listAddress[0].postalCode?:"08000",
                            city = listAddress[0].locality?:"-",
                            region = listAddress[0].adminArea?:"ad-a",
                            phone = listAddress[0].phone ?: "0",
                            countryId = listAddress[0].countryCode?:"c-cd",
                            countryName = listAddress[0].countryName,
                            countryCode = listAddress[0].countryCode,
                            countrylang = "ES",

                            )

                    viewModel.setAdressUser(address)
                    viewModel.setAddressTwo(address.address, address)
                    //, ${listAddress.get(0).postalCode}, ${listAddress.get(0).locality}, ${listAddress.get(0).countryName}
                    //}
                }
            }
        }


        viewModel.selectedPlaceAddress.observe(this) {
            binding.placeLocation.text = it.address
            binding.placeLocationText.text = it.address

            _map?.clear()

            val position = LatLng(it.lat, it.lng)

            val markerOption = MarkerOptions()
                .position(position)
            _map?.addMarker(markerOption)
            _map?.moveCamera(CameraUpdateFactory.newLatLngZoom(position, DEFAULT_MAP_ZOOM))

            // todo : I commented this lines due to no map key is provided CameraUpdateFactory need map should be initialized first
//            val cameraPosition =
//                CameraPosition.Builder().target(position).zoom(DEFAULT_MAP_ZOOM).build()
//            val cameraUpdateFactory = CameraUpdateFactory.newCameraPosition(cameraPosition)
//            _map?.animateCamera(cameraUpdateFactory)
        }

        //mostrar mis direcciones recientes (ahora no muestra)
       /** viewModel.recentAddresses.observe(this) {
            it.forEach { address ->
                val partialBinding = PartialLayoutRecentAddressBinding.inflate(layoutInflater)
                binding.recentAddressLayout.addView(partialBinding.root)

                partialBinding.currentLocation.text = address.address
                partialBinding.currentLocationText.text = "${address.postalCode}, ${address.city}, ${address.countryName}"

                partialBinding.root.setOnClickListener {
                    val additionalAddress = binding.additionalAddress.text.toString()
                    viewModel.setAddress(additionalAddress, address)
                    viewModel.setAddressInfo(additionalAddress, address)
                }
            }
        }**/

        viewModel.recentAddressesUser.observe(this) {
            it.forEach { address ->
                val partialBinding = PartialLayoutRecentAddressBinding.inflate(layoutInflater)
                binding.recentAddressLayout.addView(partialBinding.root)

                partialBinding.currentLocation.text = address.addressNumber
                partialBinding.currentLocationText.text = "${address.postalCode}, ${address.city}, ${address.countryName}"

                partialBinding.root.setOnClickListener {
                    val additionalAddress = binding.additionalAddress.text.toString()
                    println(" additionalAddress $additionalAddress")
                    viewModel.goToMain(address)

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

        viewModel.requestID.observe(this){
            return@observe
        }



        binding.placesLocationLayout.setOnClickListener {
            val additionalAddress = binding.additionalAddress.text.toString()
            viewModel.setSelectedPlaceAddress(additionalAddress)
        }

        viewModel.loadStores()

        val listCheckoutItems = loadCheckoutItem()
        val dialogo =
            AlertDialog.Builder(this)
                .setPositiveButton(getString(R.string.yes_delete)) { dialog, which ->

                    listCheckoutItems.map {
                        if (it.type == getString(R.string.type_sameday)) {

                            viewModelCart.removeItemRef(it.reference, it.type, this)
                            val itemCart = viewModel.eventsManager.itemRemoveToCart(it)
                            viewModel.eventsManager.removeFromCart(itemCart, it, it.quantity)

                        }
                    }

                }
                .setNegativeButton(
                    getString(R.string.cancel)
                ) { dialog, which ->
                    finish()
                    dialog.dismiss()

                }
                .setTitle(getString(R.string.atention))
                .setMessage(getString(R.string.postal_code_message)+
                        "\n\n"+getString(R.string.delete_products))
                .create()


        viewModel.addressChanged.observe(this){
            val sameDay = listCheckoutItems.findLast { product -> product.type == getString(R.string.type_sameday) }
            if (it){
                if (sameDay != null) {
                    dialogo.show()
                }
            }
        }

    }

    private fun loadCheckoutItem(): MutableList<CartUiModel.Item>{
        val list: MutableList<CartUiModel.Item> = mutableListOf()

        viewModel.authStore.getCart().map {

            it.toCartItemUiModel()

            list.add(

                    CartUiModel.Item(
                        productId = it.productId,
                        productName = it.product.title,
                        productPrice = it.product.combinationPrice.toString(),
                        oldPrice = it.product.oldPrice,
                        image = it.product.image,
                        quantity = it.qty,
                        discount = it.product.discount,
                        stock = it.product.stockItens?:0,
                        type= it.type,
                        reference= it.combinationReference,
                        price= it.combinationPrice,
                        brand = it.product.brand,
                        costSd = it.product.costSd,
                        costEco = it.product.costEco,
                        totalCost = it.product.totalCost,
                        samedayDelivery = it.currentTimeDelivered

                    )
                )
        }
        return list

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
          when (requestCode) {
            1 -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // aqui ya tengo permisos
                    setCurrentLocationTwo()

                } else {
                    // aqui no tengo permisos
                }
                return
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    private fun initMap() {
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )

        }
            val mapFragment =
             supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync { map ->
            _map = map

            _map?.setOnMapClickListener { latLng ->
                val listOfAddress = geoCoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

                listOfAddress.forEach { address ->
                    viewModel.setPlaceFromGeocode(address)
                }
            }
        }

        viewModel.currentAddress.observe(this) { data ->
            println(" data.address ${data.address}")
            binding.currentLocation.text = data.address
            binding.currentLocationText.text = data.address
            binding.placeLocation.text = data.address
            binding.placeLocationText.text = data.address

            _map?.clear()
            _map?.isMyLocationEnabled = true

            val position = LatLng(data.lat, data.lng)

            val markerOption = MarkerOptions()
                .position(position)
            _map?.addMarker(markerOption)
            _map?.moveCamera(CameraUpdateFactory.newLatLngZoom(position, DEFAULT_MAP_ZOOM))
            // todo : I commented this lines due to no map key is provided CameraUpdateFactory need map should be initialized first
//            val cameraPosition =
//                CameraPosition.Builder().target(position).zoom(DEFAULT_MAP_ZOOM).build()
//            val cameraUpdateFactory = CameraUpdateFactory.newCameraPosition(cameraPosition)
//            _map?.animateCamera(cameraUpdateFactory)
        }

        viewModel.currentLocation.observe(this) { latLng ->
            val listOfAddress =
                geoCoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        println(" listOfAddress $listOfAddress")

            /**listOfAddress [Address[addressLines=[0:"Carrer de la Química, 9, 08450 Llinars del Vallès, Barcelona, España"],
             * feature=9,
             * admin=Catalunya,
             * sub-admin=Barcelona,
             * locality=Llinars del Vallès,
             * thoroughfare=Carrer de la Química,
             * postalCode=08450,countryCode=ES,countryName=España,
             * hasLatitude=true,latitude=41.628941,hasLongitude=true,longitude=2.3755866,phone=null,url=null,extras=null]]**/
            listAddress = listOfAddress

            if(latLng != null) {
                _map?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        latLng,
                        DEFAULT_MAP_ZOOM),
                    )

                _map?.isMyLocationEnabled = true
            }
        }
    }

    fun isConnected(): Boolean {
        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

    private fun setupAutoCompleteSearch() {
        val adapter = PlacesAutoCompleteAdapter(this)

        binding.addressAutoComplete.setAdapter(adapter)

        binding.addressAutoComplete.doAfterTextChanged {
            jobSearchLocation?.cancel()
            if (!it.isNullOrEmpty()) {
                jobSearchLocation = lifecycleScope.launchWhenResumed {
                    binding.placesLocationLayout.visibility = View.GONE
                    binding.recentAddressLabel.text = getString(R.string.title_user_address_list)
                    //val params: ViewGroup.LayoutParams = binding.recentAddressLabel.layoutParams
                    val params: ViewGroup.LayoutParams = binding.addressLinear?.layoutParams!!
                    params.height = 950 //params.height = ViewGroup.LayoutParams.MATCH_PARENT //params.width = 100
                    binding.addressLinear?.layoutParams = params
                    try {

                    delay(500L)
                    val listOfAddress =
                        geoCoder.getFromLocationName(it.toString(), 10)

                    viewModel.loadPlacesSuggestions(listOfAddress, it.toString())

                    }catch (e: IOException){

                         if (isConnected()) {
                             println(" Internet Connected with another Error: ${e.message}")
                         }
                        else{

                            Toast.makeText(this@AddressActivity, R.string.message_conected, Toast.LENGTH_LONG).show()
                        }

                    }
                }
            }
        }

        binding.addressAutoComplete.setOnItemClickListener { _, _, _, itemId ->
            binding.addressAutoComplete.clearFocus()
            viewModel.loadPlaceFromId(itemId = itemId)
                val additionalAddress = binding.additionalAddress.text.toString()
                 viewModel.setSelectedPlaceAddress(additionalAddress)
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
                if (ActivityCompat.checkSelfPermission(
                        this@AddressActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this@AddressActivity,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    println("SIN PERMISO ")
                    Toast.makeText(
                        this@AddressActivity,
                        "Debe dar permiso de localización",
                        Toast.LENGTH_LONG
                    ).show()
                    return@launchWhenResumed
                } else {
                    fusedLocationClient.lastLocation.addOnSuccessListener(this@AddressActivity) { location ->
                        location?.let {
                            val latLng = LatLng(location.latitude, location.longitude)
                            println("location.latitude, location.longitude ${location.latitude} , ${location.longitude}")
                            val listOfAddress =
                                geoCoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

                            listOfAddress.forEach { address ->
                                viewModel.setCurrentLocationFromGeocode(address)
                            }
                        }
                    }

                }
            } catch (e: SecurityException) {
                Timber.e("Location permission not granted")
            }
        }
    }


    private fun setCurrentLocationTwo() {
        try {

            val locationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.interval = StoreLocationFragment.DELAY_FOR_CURRENT_LOCATION_TWO

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    if (locationResult == null) {
                        return
                    }

                    locationResult.locations.forEach { location ->
                        val latLng = LatLng(location.latitude, location.longitude)

                        viewModel.setCurrentLocation(latLng)
                    }

                    fusedLocationClient.removeLocationUpdates(locationCallback)
                }
            }

            fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                val location = task.result
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)

                    viewModel.setCurrentLocation(latLng)
                }else if(location == null){

                    val latLng = LatLng(40.463667, -3.74922)
                    viewModel.setCurrentLocation(latLng)

                }

                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
            }
        } catch (e: SecurityException) {
            Timber.e("Location permission not granted")
        }
    }



    companion object {
        private const val DEFAULT_MAP_ZOOM = 11f
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

}