package com.miscota.android.ui.addresscurrent

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.miscota.android.R
import com.miscota.android.address.AddressViewModel
import com.miscota.android.databinding.AddressCurrentFragmentBinding
import com.miscota.android.databinding.PartialLayoutRecentAddressCartBinding
import com.miscota.android.ui.addaddress.AddAddressFragment
import com.miscota.android.ui.tramitarpedido.TramitarPedidoFragment
import com.miscota.android.util.Address
import com.miscota.android.util.autoClean
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddressCurrentFragment : Fragment() {

    companion object {
        fun newInstance() = AddressCurrentFragment()
    }

    private lateinit var viewModel: AddressCurrentViewModel

    private val viewModelAddress by viewModel<AddressViewModel>()

    private lateinit var recentAddresses: List<Address>

    lateinit var recentAddressesUser: List<Address>

    private var binding by autoClean<AddressCurrentFragmentBinding>()

    private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddressCurrentFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.toolbar.cartHeader.text = getString(R.string.address_header_text)

        binding.toolbar.imageBack.setOnClickListener {
            fragmentManager?.popBackStackImmediate()

        }

        viewModelAddress.authStore.getUser()?.name

        loadRecentAddresses()
        loadAddressesUser()

        val bundleAddress: Bundle? = arguments

        viewModelAddress.recentAddressesUser.observe(viewLifecycleOwner) {
            it.forEach { address ->
                val partialBinding = PartialLayoutRecentAddressCartBinding.inflate(layoutInflater)
                binding.recentAddressLayout.addView(partialBinding.root)

                partialBinding.currentLocation.text = address.addressNumber
                partialBinding.currentLocationText.text = "${address.postalCode}, ${address.city}, ${address.countryName}"
                println("address $address")

                println("address groupingBy 01 ${recentAddresses.groupingBy { it }.eachCount().filter { it.value > 1 }}")
                println("address groupingBy 01 == 1 ${recentAddresses.groupingBy { it }.eachCount().filter { it.value == 1 } }")


                partialBinding.root.setOnClickListener {
                    //val additionalAddress = binding.additionalAddress.text.toString()
                    //viewModel.goToMain(address)
                    bundleAddress?.putString("addressB", address.addressNumber)
                    bundleAddress?.putString("postalCodeB", address.postalCode)
                    bundleAddress?.putString("cityB", address.city)
                    bundleAddress?.putString("provinceB", address.state)
                    bundleAddress?.putString("phoneB", address.phone.toString())

                    //val additionalAddress =  partialBinding.currentLocation.text.toString()
                    println("address partialBinding.root.setOnClickListener $address")
                    //println("additionalAddress $additionalAddress")
                    //partialBinding.currentLocation.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.background_home_new_search))
                    //partialBinding.currentLocation.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.background_home_new_search))

                    viewModelAddress.setAdressUser(address)

                    println("\naddress.addressNumber fragment ${address.addressNumber} $address")

                    val fragment = TramitarPedidoFragment()
                    fragment.arguments = bundleAddress
                    replaceFragment(fragment)
                    
                }
            }
        }

        viewModelAddress.recentAddresses.observe(viewLifecycleOwner) {
            it.forEach { address ->
                val partialBinding = PartialLayoutRecentAddressCartBinding.inflate(layoutInflater)
                binding.recentAddressLayout.addView(partialBinding.root)

                partialBinding.currentLocation.text = address.addressNumber
                partialBinding.currentLocationText.text = "${address.postalCode}, ${address.city}, ${address.countryName}"



                println("address two $address")

                println("address groupingBy 02 ${recentAddresses.groupingBy { it }.eachCount().filter { it.value > 1 }}")
                println("address groupingBy 02 == 1 ${recentAddresses.groupingBy { it }.eachCount().filter { it.value == 1 } }")

                partialBinding.root.setOnClickListener {

                    bundleAddress?.putString("addressB", address.addressNumber)
                    bundleAddress?.putString("postalCodeB", address.postalCode)
                    bundleAddress?.putString("cityB", address.city)
                    bundleAddress?.putString("provinceB", address.state)
                    bundleAddress?.putString("phoneB", address.phone.toString())

                    println("address partialBinding.root.setOnClickListener two $address")

                    viewModelAddress.setAdressUser(address)
                    println("\naddress.addressNumber fragment ${address.addressNumber} $address")

                    val fragment = TramitarPedidoFragment()
                    fragment.arguments = bundleAddress
                    replaceFragment(fragment)

                }
            }
        }


        val llBottomSheet = requireView().findViewById<LinearLayout>(R.id.bottom_sheet_add_address)
        sheetBehavior = BottomSheetBehavior.from(llBottomSheet)


        sheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {

                    }
                    /**BottomSheetBehavior.STATE_EXPANDED ->

                    BottomSheetBehavior.STATE_COLLAPSED ->

                    BottomSheetBehavior.STATE_DRAGGING -> {

                    }
                    BottomSheetBehavior.STATE_SETTLING -> {

                    }**/
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        val textBSheetInfo = requireView().findViewById<TextView>(R.id.add_address_text_info)
        textBSheetInfo.text = boldMyText(getString(R.string.text_add_address_info),36,textBSheetInfo.text.length)

        val textBSheetInfoTwo = requireView().findViewById<TextView>(R.id.add_address_text_info_two)
        textBSheetInfoTwo.text = boldMyText(getString(R.string.add_address_info),33,63)


        binding.recentAddressLayout.setOnClickListener {

            binding.addAddressBottomInfo.visibility = View.VISIBLE
            expandCloseSheet()

        }


        binding.addAddressButton.setOnClickListener {
            val fragment: Fragment = AddAddressFragment()
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.thankyou, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }


        viewModel = ViewModelProvider(this).get(AddressCurrentViewModel::class.java)
        // TODO: Use the ViewModel
    }

    private fun expandCloseSheet() {
        if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        } else if(sheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN)  {
            sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        }
        else {
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        }
    }

    private fun boldMyText(inputText:String,startIndex:Int,endIndex:Int): Spannable {
        val outPutBoldText: Spannable = SpannableString(inputText)
        outPutBoldText.setSpan(
            StyleSpan(Typeface.BOLD), startIndex, endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return outPutBoldText
    }

    fun loadRecentAddresses() {
        recentAddresses = viewModelAddress.authStore.getRecentAddresses() ?: listOf()
    }

    fun loadAddressesUser() {
        recentAddressesUser = viewModelAddress.authStore.getRecentAddressesInfo() ?: listOf()
    }


    private fun replaceFragment(fragment: Fragment) {
        fragmentManager?.beginTransaction()?.apply {
            if (fragment.isAdded) {
                show(fragment)
            } else {
                add(R.id.thankyou, fragment)
            }
            fragmentManager?.fragments?.forEach {
                if (it != fragment && it.isAdded) {
                    hide(it)
                }
            }
        }?.commit()
    }

}