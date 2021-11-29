package com.miscota.android.ui.cart

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.miscota.android.R
import com.miscota.android.address.AddressActivity
import com.miscota.android.auth.AuthActivity
import com.miscota.android.databinding.FragmentCartBinding
import com.miscota.android.util.autoClean
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class CartFragment : Fragment() {

    private var binding by autoClean<FragmentCartBinding>()

    private val viewModel by viewModel<CartViewModel>()

    private lateinit var listAdapter: CartItemAdapter

    private var costdelivered: Double = 0.0

    private var totalCartItens: Int? = null

    //private val paymentMethods: Unit = viewModelPayment.requestPaymentMethods()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var list = loadCheckout()
        var userType = false
        if (isLoggedIn())
        else
            userType = true


        var listItens = loadCheckout()
        var sum = 0
        listItens.map {
            it.qty
            println(" it.qty itens ${it.qty}")
            sum += it.qty.toInt()
            println(" it.qty sum $sum")

        }

        totalCartItens = sum
        println(" totalCartItens $totalCartItens")
        viewModel.authStore.setTotalCartItens(totalCartItens!!)


        viewModel.costDeliver.observe(viewLifecycleOwner){
            costdelivered = viewModel.costDeliver.value!!
            return@observe
        }


        binding.toolbar.imageBack.setOnClickListener {
            findNavController().navigateUp()
        }

        listAdapter =
                CartItemAdapter(
                    addItemClickListener = { cartItem ->
                        viewModel.setQuantityRef(
                            id = cartItem.productId,
                            quantity = cartItem.quantity,
                            ref = cartItem.reference,
                            stock = cartItem.stock,
                            type = cartItem.type?: getString(R.string.type_ecommerce)
                        )
                        totalCartItens = totalCartItens?.plus(1)
                        println(" totalCartItens add $totalCartItens")

                    },
                    removeItemClickListener = { cartItem ->
                        viewModel.setQuantityRef(
                            id = cartItem.productId,
                            quantity = cartItem.quantity,
                            ref = cartItem.reference,
                            stock = cartItem.stock,
                            type = cartItem.type?: getString(R.string.type_ecommerce)
                        )
                        totalCartItens = totalCartItens?.minus(1)
                        println(" totalCartItens remove $totalCartItens")
                    },
                    deleteItemClickListener = { cartItem ->
                        showDeleteConfirmationDialogRef(ref = cartItem.reference, cartItem.type?: getString(R.string.type_ecommerce), userType, cartItem)
                        println(" totalCartItens delete $totalCartItens")
                    },
                    type = { cartItem -> cartItem.type },
                    authStore = viewModel.authStore,
                    carriers =  { cartItem ->
                        cartItem.costEco.let {
                            viewModel.setCostEcoValue(viewModel.costEcommerce.value?:0.0)
                            println(" carriersSd CartFragment Adapter line 146 ${viewModel.costSd.value}")
                        }
                    },
                    carriersSd = { cartItem -> cartItem.costSd?.let { it1 ->
                        viewModel.setCarriersSd(
                            viewModel.carriersCheckoutCostSd(userType).toString()
                        )
                    }
                    },
                    types = arrayListOf("1"),
                    payment = { cartItem ->
                        cartItem.payment?.let {
                            viewModel.setPayment(
                                encryptedCard = it.encryptedCardNumber,
                                encryptedDate = cartItem.payment.encryptedExpiryMonth,
                                encryptedYear = cartItem.payment.encryptedExpiryYear,
                                encryptedSecurityCode = cartItem.payment.encryptedSecurityCode,
                                encryptedName = cartItem.payment.encryptedUserName
                            )
                        }
                    },
                    deliveredNextDay = { cartItem ->
                        showDeliveryConfirmationDialog(id = cartItem.phone?.toInt()?:0)
                    },
                    carriersItem ={viewModel.setCostEcoValue(viewModel.costEcommerce.value?:0.0)},
                    carriersSdItem = { cartItem -> cartItem.costSd?.let { it1 ->
                        viewModel.costSd.value
                    }
                    }
                 )


        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = listAdapter
        }

        viewModel.items.observe(
            viewLifecycleOwner,
            { list ->
                listAdapter.submitList(list)

                if (list.any()) {
                    binding.emptyView.visibility = View.GONE
                    binding.constraintLayout.visibility = View.GONE
                } else {
                    binding.emptyView.visibility = View.VISIBLE
                    binding.constraintLayout.visibility = View.VISIBLE
                }
            }
        )


        binding.signUpButton.setOnClickListener {
            // Example values


        }

        viewModel.costEcommerce.observe(viewLifecycleOwner){
            if (viewModel.costEcommerce.value!== null){
                costdelivered = loadCarriers(viewModel.costSd.value!!, viewModel.costEcommerce.value!!)
                println(" cart frag viewModel.costEcommerce.value ${viewModel.costEcommerce.value}")
                println(" cart frag costdelivered ecommerce $costdelivered")
                return@observe
            }
        }

        viewModel.costSd.observe(viewLifecycleOwner){
            if (viewModel.costSd.value!== null){
                costdelivered = loadCarriers(viewModel.costSd.value!!, viewModel.costEcommerce.value!!)
                println(" cart frag costdelivered sd $costdelivered")
                println(" cart frag viewModel.costSd.value ${viewModel.costSd.value}")
                return@observe
            }
        }


        viewModel.messageEvent.observe(
            viewLifecycleOwner,
            {
                it.consume()?.let { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        )

    }

    override fun onStart() {
        super.onStart()
        viewModel.loadCart()
    }

    private fun showDeliveryConfirmationDialog(id: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.remove_item_heading))
            .setMessage(getString(R.string.remove_item_msg))
            .setPositiveButton(getString(R.string.yes)) { dialogInterface, _ ->
                dialogInterface.dismiss()
                //viewModel.removeItem(id, this)
            }
            .setNegativeButton(getString(R.string.no)) { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun loadCarriers(costSd: Double, costEcommerce: Double): Double{
            var carrier = 0.0
            carrier = costSd + costEcommerce

            println(" carrier $carrier")
            return carrier
        }

    fun loadCheckout(): MutableList<CartUiModel.ItemListCheckout>{

        val list: MutableList<CartUiModel.ItemListCheckout> = mutableListOf()
        val cartItensCheckout = viewModel.authStore.getCart().map {
            it.toCartItemUiModel()

            list.add(CartUiModel.ItemListCheckout(
                qty = it.qty.toString(),
                price = it.combinationPrice.toString(),
                type = it.type,
                ref = it.combinationReference,
            ))
        }


        list.map {
            println(" it list itens cart:  $it")
        }
        return list
    }

    fun isLoggedIn(): Boolean {
        return viewModel.authStore.isLoggedIn()
    }

    fun isConnected(): Boolean {
        val cm = requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

    private fun showDeleteConfirmationDialogRef(ref: String, type: String, userType: Boolean, itemCarts: CartUiModel.Item) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.remove_item_heading))
            .setMessage(getString(R.string.remove_item_msg))
            .setPositiveButton(getString(R.string.yes)) { dialogInterface, _ ->
                dialogInterface.dismiss()
                viewModel.removeItemRef(ref, type ,requireContext())

                val list = loadCheckout()
                viewModel.carriersCheckout(userType, list, viewModel.authStore.getRetailID()?:"0")
                viewModel.setCarriersSd(viewModel.costSd.value.toString())
                viewModel.setCarriersEco(viewModel.costEcommerce.value.toString())

                val carrriers = (viewModel.costSd.value!! + viewModel.costEcommerce.value!!)
                viewModel.setCarriers(carrriers.toString())

                //firebase analytics Event removeFromCart
                val itemCart = viewModel.eventsManager.itemRemoveToCart(itemCarts)
                viewModel.eventsManager.removeFromCart(itemCart, itemCarts, itemCarts.quantity)
            }
            .setNegativeButton(getString(R.string.no)) { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            .show()
    }

}