package com.miscota.android.ui.products


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.miscota.android.MainActivity
import com.miscota.android.R
import com.miscota.android.databinding.FragmentHomeProductsBinding
import com.miscota.android.util.autoClean
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeProductsFragment : Fragment() {

    private val viewModel by viewModel<HomeProductsViewModel>()
    private var binding by autoClean<FragmentHomeProductsBinding>()
    //private lateinit var listRetail: ArrayList<Store>

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeProductsBinding.inflate(inflater, container, false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        (requireActivity() as MainActivity).binding.imageBack.isVisible = true
        (requireActivity() as MainActivity).binding.imageBack.setOnClickListener {
            //startActivity(Intent(requireContext(), MainActivity::class.java))
            //fragmentManager?.popBackStackImmediate()
            findNavController().navigateUp()
        }

        viewModel.loadSelectedLocation()

        //firebase
        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = Firebase.analytics

        viewModel.selectedLocation.observe(viewLifecycleOwner) {
            if (it != null ) {
                binding.locationText.text = it.address+ ", " + it.postalCode+ ", " + it.city ?: "N/A "
                it.postalCode.let { postalCode ->
                    viewModel.checkPostalCode(postalCode)
                    if ( viewModel.requestID.value != null){
                        println(" viewModel.requestID.value ${viewModel.requestID.value} ")
                    }
                }

            }
            /**else if (binding.locationText.text == getString(R.string.without_location)){
                Toast.makeText(requireContext(),"Facilite tu ubicación para una mejor experiencia en tus compras", Toast.LENGTH_SHORT).show()
            }**/
            /**else{
            binding.locationText.text =
                it?.address ?: getString(R.string.without_location)
            Toast.makeText(requireContext(),"Facilite la ubicación para mostrarte las tiendas disponibles", Toast.LENGTH_SHORT).show()
        }**/
        }

        viewModel.isSameDayEnabled.observe(viewLifecycleOwner) {
            autoShipStatus(it)
        }

        binding.deliveryCard.setOnClickListener {
            val samedayT = KEY_SAMEDAY
            lateinit var requestID: String
            if (viewModel.requestID.value != null){

                    requestID = viewModel.requestID.value!!.retail_shop_id

            }
            val bundleSameday = Bundle()
            bundleSameday.putString("productSameday", samedayT)
            bundleSameday.putString("requestDetailID", "5")
            viewModel.setRetailId("5")
            viewModel.setType(KEY_SAMEDAY)

            firebaseAnalytics.logEvent("screen_sameday") {
                param(FirebaseAnalytics.Param.SCREEN_NAME, "screen_sameday")
                param(FirebaseAnalytics.Param.SCREEN_CLASS, "HomeProductsFragment")
                param(FirebaseAnalytics.Param.METHOD, "deliveryCard")
            }

            findNavController().navigate(R.id.action_homeProductsFragment_to_mainCategoriesFragment, bundleSameday)
        }

        binding.allProductsCard.setOnClickListener {
            val ecommerceT = KEY_ECOMMERCE
            val bundleEcommerce = Bundle()
            bundleEcommerce.putString("productEcommerce", ecommerceT)
            bundleEcommerce.putString("requestDetailID", "5")
            viewModel.setType(KEY_ECOMMERCE)

            firebaseAnalytics.logEvent( "screen_ecommerce") {
                param(FirebaseAnalytics.Param.SCREEN_NAME, "screen_ecommerce")
                param(FirebaseAnalytics.Param.SCREEN_CLASS, "HomeProductsFragment")
                param(FirebaseAnalytics.Param.METHOD, "allProductsCard")
            }

            findNavController().navigate(R.id.action_homeProductsFragment_to_mainCategoriesFragment, bundleEcommerce)
        }
    }

    private fun autoShipStatus(enabled: Boolean) {
        binding.deliveryCard.isEnabled = enabled
        binding.autoShipDisabled.visibility = if (enabled) View.GONE else View.VISIBLE
    }

    companion object {
        const val KEY_SAMEDAY: String = "sameday"
        const val KEY_ECOMMERCE: String = "ecommerce"
    }


}