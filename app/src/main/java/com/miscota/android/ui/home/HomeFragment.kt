package com.miscota.android.ui.home

import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.miscota.android.MainActivity
import com.miscota.android.R
import com.miscota.android.address.AddressActivity
import com.miscota.android.databinding.FragmentHomeBinding
import com.miscota.android.ui.products.HomeProductsFragment
import com.miscota.android.util.autoClean
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import zendesk.chat.Chat
import zendesk.chat.ChatConfiguration
import zendesk.chat.ChatEngine
import zendesk.messaging.MessagingActivity
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment() {

    private var binding by autoClean<FragmentHomeBinding>()

    private val viewModel by viewModel<HomeViewModel>()

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var geoCoder: Geocoder

    private lateinit var chatConfiguration: ChatConfiguration

    //private lateinit var firebaseAnalytics: FirebaseAnalytics

    //private lateinit var chatConfiguration: AnswerConfiguration

   /** private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (!permissions.any { !it.value }) {
                        setCurrentLocation()
            }
        }
**/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        geoCoder = Geocoder(requireContext(), Locale.getDefault())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadAddress()

        var requestID = "0"


        if (viewModel.requestID.value?.retail_shop_id == null ) {
            binding.productsCardSameday.isEnabled = false
            binding.productsCardSameday.setCardBackgroundColor(getColor(requireContext(),R.color.grey_light_new))
            binding.productsCardSameday.isClickable = false
            requestID == getString(R.string.retail_id_default)
        }
        if ( viewModel.requestID.value?.retail_shop_id == getString(R.string.retail_id_default)) {
            binding.productsCardSameday.isEnabled = false
            binding.productsCardSameday.setCardBackgroundColor(getColor(requireContext(),R.color.grey_light_new))
            binding.productsCardSameday.isClickable = false
            requestID = viewModel.requestID.value!!.retail_shop_id
        }
        if (viewModel.requestID.value != null) {

            println("HomeFragment ${viewModel.requestID.value}")
            println("HomeFragment retail_shop ${viewModel.requestID.value!!.retail_shop_id}")

            if (viewModel.requestID.value!!.retail_shop_id != getString(R.string.retail_id_default)) {

                requestID = viewModel.requestID.value!!.retail_shop_id
                binding.productsCardSameday.isEnabled = true
                binding.productsCardSameday.setCardBackgroundColor(getColor(requireContext(),R.color.white_900))
                binding.productsCardSameday.isClickable = true

            }
            if ( viewModel.requestID.value!!.retail_shop_id == getString(R.string.retail_id_default)) {
                binding.productsCardSameday.isEnabled = false
                binding.productsCardSameday.setCardBackgroundColor(getColor(requireContext(),R.color.grey_light_new))
                binding.productsCardSameday.isClickable = false
                requestID = viewModel.requestID.value!!.retail_shop_id
            }

        }


        //***TEST******//
      /**  val navController = findNavController()
        if (navController.currentDestination?.id == R.id.navigation_product) {
            navController.navigate(R.id.navigation_home)
        }**/
        //*********//


        binding.productLabelSameday.text = colorMyText(getString(R.string.sameday_products),14,26, getColor(requireContext(), R.color.app_pink))

        viewModel.selectedLocation.observe(viewLifecycleOwner) {
            if (it != null ) {
                binding.locationText.text =
                    it.postalCode+ ", " + it.city ?: "0 "

                println(" postalCode HomeFragment new UX ${it.postalCode} , ${it.city}")

                it.postalCode.let { postalCode ->
                    viewModel.checkPostalCode(postalCode)

                    /**if ( viewModel.requestID.value != null){
                        println(" viewModel.requestID.value HomeFragment new UX ${viewModel.requestID.value} ")

                        if (viewModel.requestID.value!!.retail_shop_id != "0" ){

                            (requireActivity() as MainActivity).binding.samedayInfoMain.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.background_same_day_on))
                            (requireActivity() as MainActivity).binding.textSamedayMain.text = getString(R.string.title_sameday)
                            (requireActivity() as MainActivity).binding.textSamedayMain.text = colorMyText(getString(R.string.title_sameday),0,11, ContextCompat.getColor(requireContext(), R.color.app_pink))
                            (requireActivity() as MainActivity).binding.imageSamedayCheck.visibility = View.VISIBLE

                        }

                        if (viewModel.requestID.value!!.retail_shop_id == "0" ){

                            println(" viewModel.requestID.value HomeFragment new UX ${viewModel.requestID.value} ")

                            (requireActivity() as MainActivity).binding.samedayInfoMain.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.background_home_new_search))
                            (requireActivity() as MainActivity).binding.textSamedayMain.text = getString(R.string.same_day_off)
                            (requireActivity() as MainActivity).binding.imageSamedayCheck.visibility = View.GONE
                            (requireActivity() as MainActivity).binding.textSamedayMain.text = colorMyText(getString(R.string.same_day_off),11,37, ContextCompat.getColor(requireContext(), R.color.app_pink))

                            binding.productsCardSameday.setCardBackgroundColor(getColor(requireContext(),R.color.grey_light_new))
                        }

                    }**/
                }

            }
        }

        binding.locationLinearLayout.setOnClickListener {
            startActivity(Intent(requireContext(), AddressActivity::class.java))

           /** locationPermissionLauncher.launch(
            arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
            )
            )**/

        }

        binding.productsCard.setOnClickListener {

            viewModel.getType()
        println("  action_navigation_home_to_MainCategoriesFragment PRODUCTCARD ERROR ???")

            val ecommerceT = HomeProductsFragment.KEY_ECOMMERCE
            val bundleEcommerce = Bundle()
            bundleEcommerce.putString("productEcommerce", ecommerceT)
            bundleEcommerce.putString("requestDetailID", "0")
            viewModel.setType(HomeProductsFragment.KEY_ECOMMERCE)

            //findNavController().navigate(R.id.action_navigation_home_to_homeProductsFragment)
            findNavController().navigate(R.id.action_navigation_home_to_MainCategoriesFragment,bundleEcommerce)
        }

        viewModel.requestID.observe(viewLifecycleOwner) {
            if (viewModel.requestID.value != null) {

                println("HomeFragment ${viewModel.requestID.value}")
                println("HomeFragment retail_shop ${viewModel.requestID.value!!.retail_shop_id}")

                if (viewModel.requestID.value!!.retail_shop_id != getString(R.string.retail_id_default)) {

                    requestID = viewModel.requestID.value!!.retail_shop_id
                    binding.productsCardSameday.isEnabled = true
                    binding.productsCardSameday.setCardBackgroundColor(getColor(requireContext(),R.color.white_900))
                    (requireActivity() as MainActivity).binding.samedayInfoBottom.gravity = Gravity.START
                    (requireActivity() as MainActivity).binding.samedayInfoBottom.setPadding(50,0,0,0)
                    (requireActivity() as MainActivity).binding.samedayInfo.visibility = View.VISIBLE
                    (requireActivity() as MainActivity).binding.samedayInfoMain.setBackgroundDrawable(ContextCompat.getDrawable(requireActivity(),R.drawable.background_same_day_on))
                    binding.productsCardSameday.isClickable = true

                }
                if ( viewModel.requestID.value!!.retail_shop_id == getString(R.string.retail_id_default)) {
                    binding.productsCardSameday.isEnabled = false
                    binding.productsCardSameday.setCardBackgroundColor(getColor(requireContext(),R.color.grey_light_new))
                    binding.productsCardSameday.isClickable = false
                    (requireActivity() as MainActivity).binding.imageSamedayMain.visibility = View.VISIBLE
                    (requireActivity() as MainActivity).binding.textSamedayMain.text = getString(R.string.same_day_off)
                    (requireActivity() as MainActivity).binding.textSamedayMain.text = colorMyText(getString(R.string.same_day_off),11,37, getColor(requireContext(), R.color.app_pink))
                    (requireActivity() as MainActivity).binding.samedayInfoBottom.gravity = Gravity.START
                    (requireActivity() as MainActivity).binding.samedayInfoBottom.setPadding(50,0,0,0)
                    (requireActivity() as MainActivity).binding.samedayInfo.visibility = View.VISIBLE
                    (requireActivity() as MainActivity).binding.samedayInfoMain.setBackgroundDrawable(ContextCompat.getDrawable(requireActivity(),R.drawable.background_same_day_off))
                    (requireActivity() as MainActivity).binding.locationTextMain.text = colorMyText((requireActivity() as MainActivity).binding.locationTextMain.text.toString(),0,(requireActivity() as MainActivity).binding.locationTextMain.text.length,  getColor(requireActivity(), R.color.new_app_grey))
                    requestID = viewModel.requestID.value!!.retail_shop_id
                }

            }
            if (viewModel.requestID.value?.retail_shop_id == null ) {
                binding.productsCardSameday.isEnabled = false
                binding.productsCardSameday.setCardBackgroundColor(getColor(requireContext(),R.color.grey_light_new))
                binding.productsCardSameday.isClickable = false
            }


            val bundleSameday = Bundle()
            bundleSameday.putString("productSameday", HomeProductsFragment.KEY_SAMEDAY)
            bundleSameday.putString("requestDetailID", requestID)
            println("HomeFragment requestDetailID $requestID")


        }
        binding.productsCardSameday.setOnClickListener {

            viewModel.getType()

            println("  action_navigation_home_to_MainCategoriesFragment PRODUCTCARDSAMEDAY ERROR ???")

            val samedayT = HomeProductsFragment.KEY_SAMEDAY

            val bundleSameday = Bundle()
            bundleSameday.putString("productSameday", samedayT)
            bundleSameday.putString("requestDetailID", requestID)
            viewModel.setType(HomeProductsFragment.KEY_SAMEDAY)
            println("HomeFragment after button requestDetailID $requestID")

            findNavController().navigate(R.id.action_navigation_home_to_MainCategoriesFragment, bundleSameday)

        }

        viewModel.showAutoShip.observe(requireActivity()){
            return@observe
        }


        binding.scheduleOrderCard.setOnClickListener {

                /**if (!viewModel.isLoggedIn()) {
                    startActivity(Intent(requireContext(), AuthActivity::class.java))
                } else {
                    recordScreenShipping()
                    findNavController().navigate(R.id.action_navigation_home_to_scheduledOrderFragment)
                }**/

        }


        //deshabilitado hasta que este desarrollado la parte de consultas
        binding.consultingCard.isEnabled = true
        //binding.consultingCard.checkedIconTint?.defaultColor
        binding.consultingCard.setOnClickListener{
            //openChat()
            findNavController().navigate(R.id.action_navigation_home_to_consultingFragment)

        }

        binding.autoShipImage.setOnClickListener {
            //findNavController().navigate(R.id.action_navigation_home_to_scheduledOrderFragment)
        }
        binding.consultingCard.isEnabled = true
        binding.storeCard.setOnClickListener {
            //if(viewModel.checkIfLocationOpened()) {
                //findNavController().navigate(R.id.action_navigation_home_to_storeLocationFragment)
            findNavController().navigate(R.id.action_navigation_home_to_storeWebFragment)
        }

        viewModel.showAutoShip.observe(viewLifecycleOwner) {
            viewModel.showAutoShip.observe(viewLifecycleOwner) {
                it.consume()?.let { autoShip ->
                    //binding.bottomBackground.isVisible = true
                    //binding.nextOrderLabel.isVisible = true
                    //binding.nextOrderText.isVisible = true
                    //binding.autoShipImage.isVisible = true

                    val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val date: Date?
                    var dateNextDeliveryDate: String = "test"

                    if(autoShip.nextDeliveryDate != null) {
                        //date = fmt.parse(autoShip.nextDeliveryDate)
                     dateNextDeliveryDate = autoShip.nextDeliveryDate
                        println("autoShip.nextDeliveryDate ${autoShip.nextDeliveryDate}")
                    }else{
                        //date = fmt.parse("2020-00-01")
                        println("autoShip.nextDeliveryDate null ${autoShip.nextDeliveryDate}")
                    }

                    val fmtOut = SimpleDateFormat("dd MMM", Locale.getDefault())
                    /**binding.nextOrderText.text =
                        getString(R.string.next_order_arrives_on, dateNextDeliveryDate)**/
                        //getString(R.string.next_order_arrives_on, fmtOut.format(date))

                }
            }
        }
    }

    private fun replaceFragmentChild(fragment: Fragment) {
        fragmentManager?.beginTransaction()?.apply {
            if (fragment.isAdded) {
                show(fragment)
            } else {
                add(R.id.consulting, fragment)
            }
            fragmentManager?.fragments?.forEach {
                if (it != fragment && it.isAdded) {
                    hide(it)
                }
            }
        }?.commit()
    }

    private fun openChat() {
        Chat.INSTANCE.init(
            requireContext(),
            "0c1ce8097a5c49ffcf2a3a08e2eabce49a1f534a044f695f",
            "mobile_sdk_client_34e9f4adfefbae2d9072"
        )

        //"4fbaa9aa29fb764f77face3954c078f4d4e134f6ffd24eb5",
        //            "mobile_sdk_client_4ea97664ea7a627772c3"
        chatConfiguration = ChatConfiguration.builder()
            .withAgentAvailabilityEnabled(false)
            .build()

        MessagingActivity.builder()
            .withEngines(ChatEngine.engine())
            .show(requireContext(), chatConfiguration)
    }

    fun isConnected(): Boolean {
        val cm = requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

    private fun setCurrentLocation() {
                lifecycleScope.launchWhenResumed {
                try {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        location?.let {
                            val latLng = LatLng(location.latitude, location.longitude)
                            println("location.latitude, location.longitude homeFragment ${location.latitude} , ${location.longitude}")
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

    override fun onStart() {
        super.onStart()

        (requireActivity() as MainActivity).binding.imageBack.isVisible = false

        /**val params: ViewGroup.LayoutParams = (requireActivity() as MainActivity).binding.headerMain.layoutParams
        //params.height = 100
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT //params.width = 100
        (requireActivity() as MainActivity).binding.headerMain.layoutParams = params**/

        viewModel.loadAddress()

            viewModel.getTotalItens()
            viewModel.costEcommerce
            viewModel.costSd



        /***viewModel.requestID.observe(viewLifecycleOwner) {
            if (viewModel.requestID.value != null) {

                println("HomeFragment ${viewModel.requestID.value}")
                println("HomeFragment retail_shop ${viewModel.requestID.value!!.retail_shop_id}")

                if (viewModel.requestID.value!!.retail_shop_id != getString(R.string.retail_id_default)) {

                    requestID = viewModel.requestID.value!!.retail_shop_id
                    binding.productsCardSameday.isEnabled = true
                    binding.productsCardSameday.setCardBackgroundColor(getColor(requireContext(),R.color.white_900))
                    binding.productsCardSameday.isClickable = true

                }
                if ( viewModel.requestID.value!!.retail_shop_id == getString(R.string.retail_id_default)) {
                    binding.productsCardSameday.isEnabled = false
                    binding.productsCardSameday.setCardBackgroundColor(getColor(requireContext(),R.color.grey_light_new))
                    binding.productsCardSameday.isClickable = false
                    requestID = viewModel.requestID.value!!.retail_shop_id
                }

            }
            if (viewModel.requestID.value?.retail_shop_id == null ) {
                binding.productsCardSameday.isEnabled = false
                binding.productsCardSameday.setCardBackgroundColor(getColor(requireContext(),R.color.grey_light_new))
                binding.productsCardSameday.isClickable = false
            }


            val bundleSameday = Bundle()
            bundleSameday.putString("productSameday", HomeProductsFragment.KEY_SAMEDAY)
            bundleSameday.putString("requestDetailID", requestID)
            println("HomeFragment requestDetailID $requestID")


        }**/


    }

    private fun recordScreenShipping() {
        val screenName = "screen_shipping"

        /**firebaseAnalytics.logEvent(screenName) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, "HomeFragment")
            param(FirebaseAnalytics.Param.METHOD, "scheduleOrderCard")
        }**/

    }

    private fun colorMyText(inputText:String,startIndex:Int,endIndex:Int,textColor:Int): Spannable {
        val outPutColoredText: Spannable = SpannableString(inputText)
        outPutColoredText.setSpan(
            ForegroundColorSpan(textColor), startIndex, endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return outPutColoredText
    }


    companion object {
        const val KEY_SAMEDAY: String = "sameday"
        const val KEY_ECOMMERCE: String = "ecommerce"
    }

}
