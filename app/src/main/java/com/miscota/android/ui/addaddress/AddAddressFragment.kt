package com.miscota.android.ui.addaddress

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.miscota.android.R
import com.miscota.android.address.AddressActivity
import com.miscota.android.address.AddressViewModel
import com.miscota.android.databinding.AddAddressFragmentBinding
import com.miscota.android.ui.cart.CartViewModel
import com.miscota.android.ui.tramitarpedido.TramitarPedidoFragment
import com.miscota.android.util.Address
import com.miscota.android.util.autoClean
import com.miscota.android.util.showKeyboard
import org.koin.androidx.viewmodel.ext.android.viewModel


class AddAddressFragment : Fragment() {

    companion object {
        fun newInstance() = AddAddressFragment()
    }

    private lateinit var viewModel: AddAddressViewModel

    private var binding by autoClean<AddAddressFragmentBinding>()

    private val viewModelCart by viewModel<CartViewModel>()

    private val viewModelAddress by viewModel<AddressViewModel>()

    private lateinit var recentAddresses: List<Address>

    lateinit var recentAddressesUser: List<Address>

    private lateinit var listAddress:  MutableList<Address>

    private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddAddressFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        loadRecentAddresses()
        loadAddressesUser()

        listAddress = arrayListOf()

        binding.toolbar.cartHeader.text = getString(R.string.add_address)

        binding.toolbar.imageBack.setOnClickListener {

            getFragmentManager()?.popBackStackImmediate()

       }

        if (viewModelCart.authStore.getUser()?.phone != null){
            binding.phoneInput.setText(viewModelCart.authStore.getUser()?.phone.toString())
        }
        recentAddressesUser.map {
            it.address
            binding.postalCodeInput.setText(it.postalCode)
            //binding.postalCodeInput.isEnabled = false
        }


        val inputAddress = binding.addreesCartInput.text
        val inputPostalCode = binding.postalCodeInput.text
        val inputCity = binding.cityInput.text
        val inputProvince = binding.provinceInput.text
        val inputPhone = binding.phoneInput.text

        binding.addressCartLayout.editText?.text = inputAddress
        binding.postalCodeLayout.editText?.text = inputPostalCode
        binding.cityLayout.editText?.text = inputCity
        binding.provinceLayout.editText?.text = inputProvince
        binding.phoneLayout.editText?.text = inputPhone

        var address = inputAddress
        var postalCode = inputPostalCode
        var city = inputCity
        var province = inputProvince
        var phone = inputPhone

        var addressUSer: Address = viewModelCart.authStore.getAddress()?:Address("","",0.0,0.0,"","","","","","","","","")
        val bundleAddress: Bundle? = arguments
        binding.addreesCartInput.requestFocus()
        showKeyboard(binding.addreesCartInput)

        binding.addreesCartInput.doOnTextChanged { it, _, _, _ ->
            recentAddresses.map { it2 ->
               println("it ${it2.address}")
               println("it2 ${it2.postalCode}")
           }
            println("itss addreesCartInput $it")

            if (it != null && it.length > 3){
                address = binding.addreesCartInput.text
                println("address doafter $address")
                bundleAddress?.putString("addressB", address.toString())

                addressUSer = Address(
                    address = address.toString(),
                    addressNumber = address.toString(),
                    lat = 0.0,
                    lng = 0.0,
                    state = province.toString(), //region
                    postalCode = postalCode.toString(),
                    city = city.toString(),
                    region = province.toString(),
                    phone = phone.toString(),
                    countryId = "0 CountryId",
                    countryName = "España",
                    countryCode = "Default",
                    countrylang = "ES",

                )
            }



        }
        binding.addreesCartInput.requestFocus()
        binding.postalCodeInput.doOnTextChanged { it, _, _, _ ->

            if (it != null && it.length > 3){
                postalCode = binding.postalCodeInput.text
                println("postalCode $postalCode")
                bundleAddress?.putString("postalCodeB", postalCode.toString())

                addressUSer = Address(
                    address = address.toString(),
                    addressNumber = address.toString(),
                    lat = 0.0,
                    lng = 0.0,
                    state = province.toString(), //region
                    postalCode = postalCode.toString(),
                    city = city.toString(),
                    region = province.toString(),
                    phone = phone.toString(),
                    countryId = "0 CountryId",
                    countryName = "España",
                    countryCode = "Default",
                    countrylang = "ES",

                )
            }

        }
        binding.addreesCartInput.requestFocus()
        binding.cityInput.doOnTextChanged { it, _, _, _ ->

            if (it != null && it.length > 3){
                city = binding.cityInput.text
                println("city $city")
                bundleAddress?.putString("cityB", city.toString())

                addressUSer = Address(
                    address = address.toString(),
                    addressNumber = address.toString(),
                    lat = 0.0,
                    lng = 0.0,
                    state = province.toString(), //region
                    postalCode = postalCode.toString(),
                    city = city.toString(),
                    region = province.toString(),
                    phone = phone.toString(),
                    countryId = "0 CountryId",
                    countryName = "España",
                    countryCode = "Default",
                    countrylang = "ES",

                )
            }
        }
        binding.addreesCartInput.requestFocus()
        binding.provinceInput.doOnTextChanged { it, _, _, _ ->

            if (it != null && it.length > 3){
                province = binding.provinceInput.text
                println("province doafter $province")
                bundleAddress?.putString("provinceB", province.toString())

                addressUSer = Address(
                    address = address.toString(),
                    addressNumber = address.toString(),
                    lat = 0.0,
                    lng = 0.0,
                    state = province.toString(), //region
                    postalCode = postalCode.toString(),
                    city = city.toString(),
                    region = province.toString(),
                    phone = phone.toString(),
                    countryId = "0 CountryId",
                    countryName = "España",
                    countryCode = "Default",
                    countrylang = "ES",

               )
            }
        }

        //binding.addreesCartInput.requestFocus()
        binding.addreesCartInput.requestFocus()
        binding.phoneInput.doOnTextChanged { it, _, _, _ ->
            if (it != null && it.length == 9){
            phone = binding.phoneInput.text
                bundleAddress?.putString("phoneB", phone.toString())
                addressUSer = Address(
                    address = address.toString(),
                    addressNumber = address.toString(),
                    lat = 0.0,
                    lng = 0.0,
                    state = province.toString(), //region
                    postalCode = postalCode.toString(),
                    city = city.toString(),
                    region = province.toString(),
                    phone = phone.toString(),
                    countryId = "0 CountryId",
                    countryName = "España",
                    countryCode = "Default",
                    countrylang = "ES",

                    )
           }

        }

        listAddress.add(0, addressUSer)

        binding.addCardButtonTwo.setOnClickListener {

            viewModelAddress.setAddressInfo(address.toString(),addressUSer)

            val fragment = TramitarPedidoFragment()
            fragment.arguments = bundleAddress
            replaceFragment(fragment)
        }



        val llBottomSheet = requireView().findViewById<LinearLayout>(R.id.bottom_sheet_postal_code)
        sheetBehavior = BottomSheetBehavior.from(llBottomSheet)

        sheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {

                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })


        binding.postalCodeInput.setOnClickListener {

            binding.addressBottomInfo.visibility = View.VISIBLE

            val imm: InputMethodManager =
                requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(requireView().windowToken, 0)

            expandCloseSheet()

        }


        viewModel = ViewModelProvider(this).get(AddAddressViewModel::class.java)
        // TODO: Use the ViewModel
    }

    private fun expandCloseSheet() {
        if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            val postalCodeBottom = requireView().findViewById<androidx.cardview.widget.CardView>(R.id.postal_code_bottom)
            postalCodeBottom.setOnClickListener {
                startActivity(Intent(requireActivity(), AddressActivity::class.java))
            }


        } else if(sheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN)  {
            sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        }
        else {
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        }
    }

    fun AppCompatActivity.blockInput() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    fun loadRecentAddresses() {
        recentAddresses = viewModelCart.authStore.getRecentAddresses() ?: listOf()
    }

    fun loadAddressesUser() {
        recentAddressesUser = viewModelCart.authStore.getRecentAddressesInfo() ?: listOf()
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