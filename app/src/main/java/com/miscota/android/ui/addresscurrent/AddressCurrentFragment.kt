package com.miscota.android.ui.addresscurrent

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
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
import com.miscota.android.ui.cart.CartActivity
import com.miscota.android.ui.cart.CartUiModel
import com.miscota.android.ui.cart.CartViewModel
import com.miscota.android.ui.cart.toCartItemUiModel
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

    private val viewModelCart by viewModel<CartViewModel>()

    private lateinit var recentAddresses: List<Address>

    lateinit var recentAddressesUser: List<Address>

    private lateinit var addressUser: Address

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

        var addressSelected: Address? = null

        val bundleAddressCurrent: Bundle? = arguments
        val listCheckoutItems = loadCheckoutItem()
        val dialog =
            AlertDialog.Builder(requireContext())
                .setPositiveButton(getString(R.string.yes_delete)) { dialog, which ->

                    val newListItems: MutableList<CartUiModel.Item> = mutableListOf()
                    listCheckoutItems.map {
                        if (it.type == getString(R.string.type_sameday)) {
                            viewModelCart.removeItemRef(it.reference, it.type, requireContext())

                            val itemCart = viewModelCart.eventsManager.itemRemoveToCart(it)
                            viewModelCart.eventsManager.removeFromCart(itemCart, it, it.quantity)
                        }

                        if (it.type == getString(R.string.type_ecommerce)){
                            newListItems.add(it)
                        }
                    }
                    if ( addressSelected !=  null && newListItems.size > 0) {

                        viewModelAddress.setAdressUser(addressSelected)

                        val fragment = TramitarPedidoFragment()
                        fragment.arguments = bundleAddressCurrent
                        replaceFragment(fragment)
                    }
                    if ( addressSelected !=  null && newListItems.size == 0) {

                        viewModelAddress.setAdressUser(addressSelected)
                        startActivity(Intent(requireContext(), CartActivity::class.java))
                        requireActivity().finish()

                    }

                }
                .setNegativeButton(
                    getString(R.string.cancel)
                ) { dialog, which ->
                    fragmentManager?.popBackStackImmediate()
                    dialog.dismiss()
                }
                .setTitle(getString(R.string.atention))
                .setMessage(getString(R.string.postal_code_message)+
                        "\n\n"+getString(R.string.delete_products))
                .create()

        binding.toolbar.cartHeader.text = getString(R.string.address_header_text)

        binding.toolbar.imageBack.setOnClickListener {
            fragmentManager?.popBackStackImmediate()

        }

        viewModelAddress.authStore.getUser()?.name

        loadRecentAddresses()
        loadAddressesUser()
        loadAddress()


        //val delaySeconds = 1
        viewModelAddress.recentAddressesUser.observe(viewLifecycleOwner) {
            it.forEach { address ->
                val partialBinding = PartialLayoutRecentAddressCartBinding.inflate(layoutInflater)
                binding.recentAddressLayout.addView(partialBinding.root)

                viewModelAddress.currentAddress.value?.postalCode


                partialBinding.currentLocation.text = address.addressNumber
                partialBinding.currentLocationText.text = String.format("${address.postalCode}, ${address.city}, ${address.countryName}")

                //handler().postDelayed( {
                partialBinding.root.setOnClickListener {

                    bundleAddressCurrent?.putString("addressBC", address.addressNumber)
                    bundleAddressCurrent?.putString("postalCodeBC", address.postalCode)
                    bundleAddressCurrent?.putString("cityBC", address.city)
                    bundleAddressCurrent?.putString("provinceBC", address.state)
                    bundleAddressCurrent?.putString("phoneBC", address.phone.toString())

                    //val additionalAddress =  partialBinding.currentLocation.text.toString()
                    partialBinding.currentLocation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_checkbox_true, 0, 0, 0)

                    val sameDay = listCheckoutItems.findLast { product -> product.type == getString(R.string.type_sameday) }


                    println("address.postalCode 11 ${address.postalCode} addressUser.postalCode ${addressUser.postalCode} sameDay $sameDay ")

                    if (address.postalCode != addressUser.postalCode && sameDay != null) {

                        println("address.postalCode 11 ${address.postalCode} addressUser.postalCode ${addressUser.postalCode} sameDay != null ")

                        dialog.show()
                        addressSelected = address
                    }
                    if (address.postalCode != addressUser.postalCode && sameDay == null)
                    {
                        viewModelAddress.setAdressUser(address)

                        println("address.postalCode 11 ${address.postalCode} addressUser.postalCode ${addressUser.postalCode} sameDay == null ")

                        bundleAddressCurrent?.putString("addressBC", address.addressNumber)
                        bundleAddressCurrent?.putString("postalCodeBC", address.postalCode)
                        bundleAddressCurrent?.putString("cityBC", address.city)
                        bundleAddressCurrent?.putString("provinceBC", address.state)
                        bundleAddressCurrent?.putString("phoneBC", address.phone.toString())

                        val fragment = TramitarPedidoFragment()
                        fragment.arguments = bundleAddressCurrent
                        replaceFragment(fragment)

                    }

                    if (address.postalCode == addressUser.postalCode && sameDay == null)
                    {
                        viewModelAddress.setAdressUser(address)

                        println("address.postalCode 11 ${address.postalCode} addressUser.postalCode ${addressUser.postalCode} sameDay == null ")

                        bundleAddressCurrent?.putString("addressBC", address.addressNumber)
                        bundleAddressCurrent?.putString("postalCodeBC", address.postalCode)
                        bundleAddressCurrent?.putString("cityBC", address.city)
                        bundleAddressCurrent?.putString("provinceBC", address.state)
                        bundleAddressCurrent?.putString("phoneBC", address.phone.toString())

                        val fragment = TramitarPedidoFragment()
                        fragment.arguments = bundleAddressCurrent
                        replaceFragment(fragment)

                    }
                    if (address.postalCode == addressUser.postalCode && sameDay != null)
                    {
                        viewModelAddress.setAdressUser(address)

                        println("address.postalCode 11 ${address.postalCode} addressUser.postalCode ${addressUser.postalCode} sameDay == null ")

                        bundleAddressCurrent?.putString("addressBC", address.addressNumber)
                        bundleAddressCurrent?.putString("postalCodeBC", address.postalCode)
                        bundleAddressCurrent?.putString("cityBC", address.city)
                        bundleAddressCurrent?.putString("provinceBC", address.state)
                        bundleAddressCurrent?.putString("phoneBC", address.phone.toString())

                        val fragment = TramitarPedidoFragment()
                        fragment.arguments = bundleAddressCurrent
                        replaceFragment(fragment)

                    }
                }
            }
        }

        viewModelAddress.recentAddresses.observe(viewLifecycleOwner) {
            it.forEach { address ->
                val partialBinding = PartialLayoutRecentAddressCartBinding.inflate(layoutInflater)
                binding.recentAddressLayout.addView(partialBinding.root)
                partialBinding.currentLocation.text = address.addressNumber
                partialBinding.currentLocationText.text = String.format("${address.postalCode}, ${address.city}, ${address.countryName}")

                viewModelAddress.currentAddress.value?.postalCode

                partialBinding.root.setOnClickListener {

                    partialBinding.currentLocation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_checkbox_true, 0, 0, 0)

                    val sameDay = listCheckoutItems.findLast { product -> product.type == getString(R.string.type_sameday) }

                    println("address.postalCode 22 ${address.postalCode} addressUser.postalCode ${addressUser.postalCode} sameDay $sameDay ")

                    if (address.postalCode != addressUser.postalCode && sameDay != null) {

                        println("address.postalCode 22 ${address.postalCode} addressUser.postalCode ${addressUser.postalCode} sameDay != null ")

                        dialog.show()
                        addressSelected = address
                    }
                    if (address.postalCode != addressUser.postalCode && sameDay == null)
                    {
                            viewModelAddress.setAdressUser(address)

                        println("address.postalCode 22 ${address.postalCode} addressUser.postalCode ${addressUser.postalCode} sameDay == null ")

                            bundleAddressCurrent?.putString("addressBC", address.addressNumber)
                            bundleAddressCurrent?.putString("postalCodeBC", address.postalCode)
                            bundleAddressCurrent?.putString("cityBC", address.city)
                            bundleAddressCurrent?.putString("provinceBC", address.state)
                            bundleAddressCurrent?.putString("phoneBC", address.phone.toString())

                            val fragment = TramitarPedidoFragment()
                            fragment.arguments = bundleAddressCurrent
                            replaceFragment(fragment)

                    }
                    if (address.postalCode == addressUser.postalCode && sameDay == null)
                    {
                        viewModelAddress.setAdressUser(address)

                        println("address.postalCode 11 ${address.postalCode} addressUser.postalCode ${addressUser.postalCode} sameDay == null ")

                        bundleAddressCurrent?.putString("addressBC", address.addressNumber)
                        bundleAddressCurrent?.putString("postalCodeBC", address.postalCode)
                        bundleAddressCurrent?.putString("cityBC", address.city)
                        bundleAddressCurrent?.putString("provinceBC", address.state)
                        bundleAddressCurrent?.putString("phoneBC", address.phone.toString())

                        val fragment = TramitarPedidoFragment()
                        fragment.arguments = bundleAddressCurrent
                        replaceFragment(fragment)

                    }
                    if (address.postalCode == addressUser.postalCode && sameDay != null)
                    {
                        viewModelAddress.setAdressUser(address)

                        println("address.postalCode 11 ${address.postalCode} addressUser.postalCode ${addressUser.postalCode} sameDay == null ")

                        bundleAddressCurrent?.putString("addressBC", address.addressNumber)
                        bundleAddressCurrent?.putString("postalCodeBC", address.postalCode)
                        bundleAddressCurrent?.putString("cityBC", address.city)
                        bundleAddressCurrent?.putString("provinceBC", address.state)
                        bundleAddressCurrent?.putString("phoneBC", address.phone.toString())

                        val fragment = TramitarPedidoFragment()
                        fragment.arguments = bundleAddressCurrent
                        replaceFragment(fragment)

                    }

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

    private fun loadAddressesUser() {
        recentAddressesUser = viewModelAddress.authStore.getRecentAddressesInfo() ?: listOf()
    }

    fun loadAddress(){
        addressUser =
            (viewModelAddress.authStore.getAddress()?:viewModelAddress.authStore.getAddressInfo()?: Address("","",0.0,0.0,"","","","","","","","",""))
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


    private fun loadCheckoutItem(): MutableList<CartUiModel.Item>{

        val list: MutableList<CartUiModel.Item> = mutableListOf()

        viewModelCart.authStore.getCart().map {

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

    private fun boldColorMyText(inputText:String,startIndex:Int,endIndex:Int,textColor:Int): Spannable {
        val outPutBoldColorText: Spannable = SpannableString(inputText)
        outPutBoldColorText.setSpan(
            StyleSpan(Typeface.BOLD), startIndex, endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        outPutBoldColorText.setSpan(
            ForegroundColorSpan(textColor), startIndex, endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return outPutBoldColorText
    }

}
