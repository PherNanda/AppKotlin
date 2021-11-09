package com.miscota.android.ui.pedido

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.miscota.android.MainActivity
import com.miscota.android.R
import com.miscota.android.databinding.PedidoFragmentBinding
import com.miscota.android.ui.cart.CartUiModel
import com.miscota.android.ui.cart.CartViewModel
import com.miscota.android.ui.cart.toCartItemUiModel
import com.miscota.android.ui.cart.toCartUiModel
import com.miscota.android.ui.paymentmethod.PaymentMethodFragment
import com.miscota.android.ui.productdetail.CartProduct
import com.miscota.android.ui.webview.WebViewFragment
import com.miscota.android.util.Address
import com.miscota.android.util.autoClean
import org.koin.android.ext.android.bind
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.ArrayList

class Pedido : Fragment() {

    companion object {
        fun newInstance() = Pedido()
    }

    private lateinit var viewModel: PedidoViewModel
    private var binding by autoClean<PedidoFragmentBinding>()

    private val viewModelCart by viewModel<CartViewModel>()
    private lateinit var listAdapter: PedidoImageAdapter

    private lateinit var recentAddresses: List<Address>

    lateinit var recentAddressesUser: List<Address>

    private lateinit var address: Address

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PedidoFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val bundleEcommerce: Bundle? = arguments
        val pedido: String? = bundleEcommerce?.getString(getString(R.string.get_order_ref_number))

        loadCartToItem()

        loadRecentAddresses()
        loadAddressesUser()
        loadAddress()


        binding.userNameThankYou.text = viewModelCart.authStore.getUser()?.name
        binding.userTextInfoOrder.text = "${viewModelCart.authStore.getUser()?.name.toString()}${getString(R.string.order_user_message_two)}"

        println(" viewModelCart.authStore.getUser()?.name ${viewModelCart.authStore.getUser()?.name}")

        binding.orderNumber.text = pedido

        /**binding.imageClose.setOnClickListener {
            getFragmentManager()?.beginTransaction()?.remove(this)?.commit()
        }**/

        recentAddresses.map {
            it.address
            binding.addressThankYou.text = it.addressNumber
            binding.addressComplementThankYou.text = "${it.postalCode}, ${it.city}, ${it.state}, ${it.countryName}"

            println("recentAddresses it.address thankYou ${it.address}")
        }

        recentAddressesUser.map {
            it.address
            println("recentAddressesUser it.address thankYou ${it.address}")
        }

        if(address != null){
            binding.addressThankYou.text = address.addressNumber
            binding.addressComplementThankYou.text = "${address.postalCode}, ${address.city}, ${address.state}, ${address.countryName}"

            println("address it.address thankYou ${address.address}")
        }

        val listCheckoutProducts = loadCheckout()

        binding.goToProductsHome.setOnClickListener {
            listCheckoutProducts.map {
                viewModelCart.removeItemRef(it.ref, it.type?: getString(R.string.type_ecommerce),requireContext())
            }
            startActivity(Intent(requireContext(), MainActivity::class.java))
        }

        binding.goToUserZoneImageButton.setOnClickListener {

            //val navigationProfile = view?.findViewById<BottomNavigationItemView>(R.id.navigation_profile)

            /**findNavController().navigate(
                R.id.action_pedidoFragment_to_webViewFragmentProfile, bundleOf(
                    WebViewFragment.KEY_STRING_WEB_VIEW_URL to it
                )
            )**/

            //navigationProfile?.setOnClickListener {
                //startActivity(Intent(requireContext(), MainActivity::class.java)) to navigationProfile
                //navController.navigate(R.id.navigation_home)
            //}


            listCheckoutProducts.map {
                viewModelCart.removeItemRef(it.ref, it.type?: getString(R.string.type_ecommerce),requireContext())
            }
            startActivity(Intent(requireContext(), MainActivity::class.java))
        }
        binding.goToUserZoneText.setOnClickListener {

            //val navigationOrders = view?.findViewById<BottomNavigationItemView>(R.id.navigation_orders)

            /**navigationOrders?.setOnClickListener {
                startActivity(Intent(this, MainActivity::class.java))
                //navController.navigate(R.id.navigation_home)
            }**/

            listCheckoutProducts.map {
                viewModelCart.removeItemRef(it.ref, it.type?: getString(R.string.type_ecommerce),requireContext())
            }
            startActivity(Intent(requireContext(), MainActivity::class.java))
        }

        val listTwo = viewModelCart.authStore.getCart()
        val listImage = ArrayList<CartProduct>()
        listTwo.map {
            println("CartItem tramitar pedido ${it.product.image}")
            //listImage.add(it)
        }

        listAdapter =
            PedidoImageAdapter(
                context = requireActivity(),
                itensCart = listTwo,
                numberImage = intArrayOf(),
                authStore = viewModelCart.authStore
            )


        binding.recyclerViewProducts.apply {
            layoutManager = LinearLayoutManager(context,
                LinearLayoutManager.HORIZONTAL,
                false)
            adapter = listAdapter
        }

        viewModel = ViewModelProvider(this).get(PedidoViewModel::class.java)

    }

    fun loadCheckout(): MutableList<CartUiModel.ItemListCheckout>{

        val list: MutableList<CartUiModel.ItemListCheckout> = mutableListOf()
        viewModelCart.authStore.getCart().map {
            it.toCartItemUiModel()

            list.add(
                CartUiModel.ItemListCheckout(
                    qty = it.qty.toString(),
                    price = it.combinationPrice.toString(),
                    type = it.type,
                    ref = it.combinationReference,
                )
            )

        }
        return list
    }

    fun loadCartToItem() {
        val list: MutableList<CartUiModel> = mutableListOf()
        var totalSameDay = 0
        var totalEcommerce = 0
        var currentDelivered: String? = null
        val cartItems = viewModelCart.authStore.getCart().map {
            println("imagen ${it.toCartUiModel().image}")
            println("type ${it.toCartUiModel().type}")
            println("quantity ${it.toCartUiModel().quantity}")
            println("productName ${it.toCartUiModel().productName}")
            println("samedayDelivery ${it.toCartUiModel().samedayDelivery}")

            it.toCartItemUiModel()

            if (it.toCartUiModel().type == getString(R.string.type_sameday)){
                totalSameDay += it.toCartUiModel().quantity
            }
            if (it.toCartUiModel().type == getString(R.string.type_ecommerce)){
                totalEcommerce += it.toCartUiModel().quantity
            }
            if (it.toCartUiModel().samedayDelivery != null && it.toCartUiModel().type == getString(R.string.type_sameday)){
                currentDelivered = it.toCartUiModel().samedayDelivery
                binding.dateOrderReceiveSameDay.text = it.toCartUiModel().samedayDelivery
            }
            if (it.toCartUiModel().samedayDelivery == "" && it.toCartUiModel().type == getString(R.string.type_sameday)){
                binding.dateOrderReceiveSameDay.text = " >Select hoy"
            }

        }

        println("totalSameDay::: $totalSameDay")
        println("totalEcommerce:::: $totalEcommerce")

        if ( totalSameDay > 0 ){
            binding.totalProductsCartSameDay.text = String.format(resources.getString(R.string.total_products_exemple),totalSameDay)
            println("currentDelivered $currentDelivered")

        }
        if ( totalEcommerce > 0 ){

            binding.totalProductsCartEcommerce.text = String.format(resources.getString(R.string.total_products_exemple),totalEcommerce.toString())

        }
        if ( totalSameDay == 0 ){
            binding.totalProductsCartSameDay.text = String.format(resources.getString(R.string.total_products_exemple),"0")
            binding.dateOrderReceiveSameDay.visibility = View.GONE
            binding.orderInfoSameDay.visibility = View.GONE
            binding.totalProductsCartSameDay.visibility = View.GONE
            binding.typeOrderSameday.visibility = View.GONE
            binding.imageSameday.visibility = View.GONE
        }
        if ( totalEcommerce == 0 ){
            binding.totalProductsCartEcommerce.text = String.format(resources.getString(R.string.total_products_exemple),"0")
            binding.orderEcommerceTitle.visibility = View.GONE
            binding.totalProductsCartEcommerce.visibility = View.GONE
        }

        val totalProducts = totalEcommerce.plus(totalSameDay)

        binding.productsLabelCartText.text = totalProducts.toString()

    }

    fun loadRecentAddresses() {
        recentAddresses = viewModelCart.authStore.getRecentAddresses() ?: listOf()
    }

    fun loadAddressesUser() {
        recentAddressesUser = viewModelCart.authStore.getRecentAddressesInfo() ?: listOf()
    }

    fun loadAddress() {
        address = viewModelCart.authStore.getAddress()?: Address("","",0.0,0.0,"","","","","","","","","")
    }

}