package com.miscota.android.ui.store

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.miscota.android.MainActivity
import com.miscota.android.R
import com.miscota.android.databinding.FragmentStoreLocationBinding
import com.miscota.android.util.autoClean
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class StoreLocationFragment : Fragment() {

    private var binding by autoClean<FragmentStoreLocationBinding>()

    private var _map by autoClean<GoogleMap>()

    private val viewModel: StoreLocationViewModel by viewModel()

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var firebaseAnalytics: FirebaseAnalytics

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

    private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (!permissions.any { !it.value }) {
                //setCurrentLocation()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )

        val mainActivity = requireActivity() as MainActivity
        mainActivity.binding.imageBack.isVisible = false
        mainActivity.binding.logoImage.isVisible = false
        mainActivity.binding.storeImage.isVisible = false
        mainActivity.binding.cartItemsText.isVisible = false
        mainActivity.binding.cartItemsText.text = viewModel.authStore.getTotalCartItens().toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStoreLocationBinding.inflate(inflater, container, false)
        binding.mapView.onCreate(savedInstanceState)

        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

        initMap()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //firebase
        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = Firebase.analytics

        binding.imageBackButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.searchEditText.setOnKeyListener(
            View.OnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                    val imm: InputMethodManager =
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)

                    val location = viewModel.geoCodeInput(binding.searchEditText.text.toString())
                    if (location != null) {
                        var latlng = LatLng(location.latitude, location.longitude)
                        _map.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                        latlng,
                                        MAP_SELECTED_MARKER_ZOOM,
                                )
                        )
                        //firebase event
                        recordFindShop()
                    }

                    return@OnKeyListener true
                }
                false
            }
        )

        binding.searchEditText.doAfterTextChanged {

        }

        viewModel.messageEvent.observe(
            viewLifecycleOwner,
            {
                it.consume()?.let { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        )

        viewModel.spinnerLoading.observe(viewLifecycleOwner) {
            binding.spinner.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.loadStores()
    }

    private fun initMap() {
        binding.mapView.getMapAsync { map ->
            _map = map

            _map.setOnMapClickListener {
                _map.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        it,
                        MAP_SELECTED_MARKER_ZOOM,
                    )
                )
            }

            viewModel.currentLocation.observe(viewLifecycleOwner) { latLng ->
                if(latLng != null) {
                    _map.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            latLng,
                            MAP_SELECTED_MARKER_ZOOM,
                        )
                    )

                    /*_map.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        marker.position,
                        MAP_SELECTED_MARKER_ZOOM
                    )
                )*/
                }
            }

            binding.currentLocationImage.setOnClickListener {
                if(viewModel.checkIfLocationOpened()) {
                viewModel.triggerCurrentLocation()
                }else{
                    Toast.makeText(requireContext(),"Debes habilitar la localización para poder encontrar una tienda cercana",Toast.LENGTH_LONG).show()
                }
            }

            val adapter = StoreLocationsAdapter(
                itemClickListener = {
                    _map.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(it.latitude, it.longitude),
                            MAP_SELECTED_MARKER_ZOOM
                        )
                    )
                    viewModel.setItemSelected(it)
                }
            )

            binding.recyclerView.adapter = adapter
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerView.itemAnimator = null

            viewModel.dataList.observe(viewLifecycleOwner) {
                binding.bottomLayout.isVisible = !it.isNullOrEmpty()
                adapter.submitList(it)
                _map.clear()
                it.forEach { store ->
                    val marker = MarkerOptions()
                        .position(LatLng(store.latitude, store.longitude))
                        .icon(
                            bitmapDescriptorFromVector(
                                requireContext(),
                                R.drawable.ic_location,
                            )
                        )
                    _map.addMarker(marker)
                }
            }
        }

    }

    override fun onStart() {
        binding.mapView.onStart()
        super.onStart()
    }

    override fun onResume() {
        binding.mapView.onResume()
        super.onResume()
    }

    override fun onPause() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        binding.mapView.onPause()
        super.onPause()
    }

    override fun onStop() {
        binding.mapView.onStop()
        super.onStop()
    }

    @Suppress("DEPRECATION")
    override fun onDestroyView() {
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        val mainActivity = requireActivity() as MainActivity
        mainActivity.binding.imageBack.isVisible = true
        mainActivity.binding.logoImage.isVisible = true
        mainActivity.binding.storeImage.isVisible = true
        mainActivity.binding.cartItemsText.isVisible = true
        mainActivity.binding.cartItemsText.text = viewModel.authStore.getTotalCartItens().toString()
        super.onDestroyView()
    }

    override fun onDestroy() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        super.onDestroy()
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap =
                Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    private fun setCurrentLocation() {
        try {

            val locationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.interval = DELAY_FOR_CURRENT_LOCATION_TWO

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
                }

                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
            }
        } catch (e: SecurityException) {
            Timber.e("Location permission not granted")
        }
    }

    private fun recordFindShop() {
        val screenName = "screen_find_shop"

        firebaseAnalytics.logEvent(screenName) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, "StoreLocationFragment")
            param(FirebaseAnalytics.Param.METHOD, "recordFindShop")
        }

    }

    companion object {
        const val DELAY_FOR_CURRENT_LOCATION = 1000L * 60 * 60
        const val DELAY_FOR_CURRENT_LOCATION_TWO = 1000L * 20 * 20
        const val MAP_SELECTED_MARKER_ZOOM = 10f
    }
}
