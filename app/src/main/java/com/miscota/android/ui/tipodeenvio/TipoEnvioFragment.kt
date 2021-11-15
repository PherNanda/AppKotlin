package com.miscota.android.ui.tipodeenvio

import android.R.attr.left
import android.R.attr.right
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.miscota.android.R
import com.miscota.android.databinding.TipoEnvioFragmentBinding
import com.miscota.android.ui.cart.CartActivity
import com.miscota.android.ui.cart.CartViewModel
import com.miscota.android.ui.tramitarpedido.TramitarPedidoFragment
import com.miscota.android.util.CartItem
import com.miscota.android.util.autoClean
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class TipoEnvioFragment : Fragment() {

    companion object {
        fun newInstance() = TipoEnvioFragment()
    }

    private lateinit var viewModel: TipoEnvioViewModel
    private var binding by autoClean<TipoEnvioFragmentBinding>()

    private lateinit var listAdapter: TipoEnvioProductListAdapter

    private val viewModelCart by viewModel<CartViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TipoEnvioFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        listAdapter.hasObservers()

        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss a")
        val currentDate = sdf.format(Date())
        val dateFormat: DateFormat = DateFormat.getDateInstance()
        println(" currentDate $currentDate") //currentDate 20/8/2021 04:24:20 p.m.
        println(" currentDate[0] ${currentDate[0]}")
        println(" currentDate.split ${currentDate.split(" ").firstOrNull()}")


        val start = Calendar.getInstance()

        println(" start.time.hours tipo envio ${start.time.hours} start.time.minutes ${start.time.minutes} ")
        println(" start.time tipo envio ${start.time} start.time.minutes ${start.time.minutes} ")
        println(" start.time.time tipo envio ${start.time.time} start.time.minutes ${start.time.minutes} ")
        println(" start.time.date tipo envio ${start.time.date} start.time.minutes ${start.time.minutes} ") //start.time.date tipo envio 20 start.time.minutes 42
        println(" start.time.day tipo envio ${start.time.day} start.time.minutes ${start.time.minutes} ") //start.time.day tipo envio 5 start.time.minutes 42  (viernes)
        println(" start.time.month tipo envio ${start.time.month} start.time.minutes ${start.time.minutes} ")
        println(" start.time.year tipo envio ${start.time.year} start.time.minutes ${start.time.minutes} ")

        binding.toolbar.cartHeader.text = getString(R.string.tipo_envio_header_text)
        //(requireActivity() as MainActivity).binding.imageBack.isVisible = true


        val day = 1
        val valores = arrayListOf<String>()
        val startDay = Calendar.getInstance()
        startDay.add(Calendar.DATE, day)

        val dat: Date = startDay.time
        val currentDateN = sdf.format(dat)
        val dateFormatN: DateFormat = DateFormat.getDateInstance()


        if ((start.time.hours <= 9 && start.time.minutes >= 0)
            || (start.time.hours <= 9 && start.time.minutes <= 59)
        ) {
            val day = 1
            val startDayOne = Calendar.getInstance()

            if (startDayOne.time.day == 0) {

                startDayOne.add(Calendar.DATE, day)
                val dat: Date = startDayOne.time
                val currentDateNextInSunday = sdf.format(dat)

                valores.add(
                    currentDateNextInSunday.split(" ").firstOrNull() + " " +
                            getString(R.string.receive_between_11_00_a_m_and_14_00_p_m_nd)
                )
                //deliveryType("T1ND")

            }else {
                valores.add(
                    currentDate.split(" ").firstOrNull() + " " +
                            getString(R.string.receive_between_11_00_a_m_and_14_00_p_m_sd)
                )
                //deliveryType("T1SD")
            }
        }
        if ((start.time.hours <= 13 && start.time.minutes >= 0)
            || (start.time.hours <= 13 && start.time.minutes <= 59)
        ) {

            val day = 1
            val startDayOne = Calendar.getInstance()

            if (startDayOne.time.day == 0) {

                startDayOne.add(Calendar.DATE, day)
                val dat: Date = startDayOne.time
                val currentDateNextInSunday = sdf.format(dat)

                valores.add(
                    currentDateNextInSunday.split(" ").firstOrNull() + " " +
                            getString(R.string.receive_between_15_00_a_m_and_18_00_p_m_nd)
                )
                //deliveryType("T2ND")

            }else {
                valores.add(
                    currentDate.split(" ").firstOrNull() + " " +
                            getString(R.string.receive_between_15_00_a_m_and_18_00_p_m_sd)
                )
                //deliveryType("T2SD")
            }
        }
        if ((start.time.hours <= 18 && start.time.minutes >= 0)
            || (start.time.hours <= 18 && start.time.minutes <= 59)
        ) {

            val day = 1
            val startDayOne = Calendar.getInstance()

            if (startDayOne.time.day == 0) {

                startDayOne.add(Calendar.DATE, day)
                val dat: Date = startDayOne.time
                val currentDateNextInSunday = sdf.format(dat)

                valores.add(
                    currentDateNextInSunday.split(" ").firstOrNull() + " " +
                            getString(R.string.receive_between_20_00_a_m_and_22_00_p_m_nd)
                )
                //deliveryType("T3ND")

            }else {

                valores.add(
                    currentDate.split(" ").firstOrNull() + " " +
                            getString(R.string.receive_between_20_00_a_m_and_22_00_p_m_sd)
                )
                //deliveryType("T3SD")
            }
        }
        if ((start.time.hours >= 19 && start.time.minutes > 0) ||
            (start.time.hours >= 14 && start.time.minutes > 0)
        ) {

            val startDay3 = Calendar.getInstance()

            val saturdayDay: Int = 2
            if (startDay3.time.day == 6) {
                startDay3.add(Calendar.DATE, saturdayDay)

                val dat: Date = startDay3.time
                val currentDateNextInSaturday = sdf.format(dat)

                println("currentDateNextInSaturday     $currentDateNextInSaturday")
                valores.add(currentDateNextInSaturday.split(" ").firstOrNull()+" "+
                        getString(R.string.receive_between_11_00_a_m_and_14_00_p_m_nextDay))
                valores.add(currentDateNextInSaturday.split(" ").firstOrNull()+" "+
                        getString(R.string.receive_between_15_00_a_m_and_18_00_p_m_nextDay))
                valores.add(currentDateNextInSaturday.split(" ").firstOrNull()+" "+
                        getString(R.string.receive_between_20_00_a_m_and_22_00_p_m_nextDay))

            }else{

                valores.add(currentDateN.split(" ").firstOrNull()+" "+
                    getString(R.string.receive_between_11_00_a_m_and_14_00_p_m_nextDay))
                valores.add(currentDateN.split(" ").firstOrNull()+" "+
                    getString(R.string.receive_between_15_00_a_m_and_18_00_p_m_nextDay))
                valores.add(currentDateN.split(" ").firstOrNull()+" "+
                    getString(R.string.receive_between_20_00_a_m_and_22_00_p_m_nextDay))

            }

        }


        val listTwo = viewModelCart.authStore.getCart()


        for (cartItem in listTwo) {
            if (cartItem.type == getString(R.string.type_sameday)){
                binding.selectReceiveSameday.visibility = View.VISIBLE
            }
        }

        if (binding.selectReceiveSameday.visibility == View.GONE){
            val params = (binding.titleTypeOrder.layoutParams as ViewGroup.MarginLayoutParams)
            params.setMargins(0, 150, 0, 0)
            binding.titleTypeOrder.layoutParams = params
        }


        listAdapter =
            TipoEnvioProductListAdapter(
                context = requireActivity(),
                itensCart = listTwo,
                numberImage = intArrayOf(),
                authStore = viewModelCart.authStore
            )


        binding.recyclerViewTypeOrders.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = listAdapter
        }

        val currentTimeBundle = Bundle()
        binding.spinnerSelectSameDayReceive.adapter =
            ArrayAdapter<String>(requireActivity(), R.layout.item_select_date, valores)
        binding.spinnerSelectSameDayReceive.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                Toast.makeText(
                    adapterView.context,
                    adapterView.getItemAtPosition(position) as String,
                    Toast.LENGTH_SHORT
                ).show()
                loadCart(adapterView.getItemAtPosition(position) as String)

                println("adapterView.getItemAtPosition(position) as String ${adapterView.getItemAtPosition(position) as String}")
                val userSelection = adapterView.getItemAtPosition(position) as String

                println("userSelection.split(\"de\").firstOrNull():: ${userSelection.split("de").firstOrNull()}")

                val allInfoSelection = userSelection.split("de").toTypedArray()
                val dateSelection = userSelection.split("de").firstOrNull()

                println("dateSelection $dateSelection")
                println("allInfoSelection $allInfoSelection")
                println("allInfoSelection[1] ${allInfoSelection[1]}")

                println("de+allInfoSelection[1] de${allInfoSelection[1]}")

                val mySelection = "de"+allInfoSelection[1]
                var deliveryType = "DEFAULT"

                if( mySelection == getString(R.string.receive_between_11_00_a_m_and_14_00_p_m_nd) ){

                    deliveryType = "T1ND"
                    deliveryType("T1ND")
                    println("my selection de${allInfoSelection[1]}")
                }
                if( mySelection == getString(R.string.receive_between_11_00_a_m_and_14_00_p_m_sd) ){

                    deliveryType = "T1SD"
                    deliveryType("T1SD")
                    println("my selection de${allInfoSelection[1]}")
                }
                if( mySelection == getString(R.string.receive_between_15_00_a_m_and_18_00_p_m_nd) ){

                    deliveryType = "T2ND"
                    deliveryType("T2ND")
                    println("my selection de${allInfoSelection[1]}")
                }
                if( mySelection == getString(R.string.receive_between_15_00_a_m_and_18_00_p_m_sd) ){

                    deliveryType = "T2SD"
                    deliveryType("T2SD")
                    println("my selection de${allInfoSelection[1]}")
                }
                if( mySelection == getString(R.string.receive_between_20_00_a_m_and_22_00_p_m_nd) ){

                    deliveryType = "T3ND"
                    deliveryType("T3ND")
                    println("my selection de${allInfoSelection[1]}")
                }
                if( mySelection == getString(R.string.receive_between_20_00_a_m_and_22_00_p_m_sd) ){

                    deliveryType = "T3SD"
                    deliveryType("T3SD")
                    println("my selection de${allInfoSelection[1]}")
                }
                if ( mySelection == getString(R.string.receive_between_11_00_a_m_and_14_00_p_m_nextDay) ){

                    deliveryType = "T1ND"
                    deliveryType("T1ND")
                    println("my selection de${allInfoSelection[1]}")

                }
                if ( mySelection == getString(R.string.receive_between_15_00_a_m_and_18_00_p_m_nextDay) ){

                    deliveryType = "T2ND"
                    deliveryType("T2ND")
                    println("my selection de${allInfoSelection[1]}")

                }
                if ( mySelection == getString(R.string.receive_between_20_00_a_m_and_22_00_p_m_nextDay) ){

                    deliveryType = "T3ND"
                    deliveryType("T3ND")
                    println("my selection de${allInfoSelection[1]}")

                }

                currentTimeBundle.putString(
                    getString(R.string.date_shipping_sameday),
                    adapterView.getItemAtPosition(position) as String
                )
                currentTimeBundle.putString(
                    getString(R.string.delivery_type_sameday),
                    deliveryType
                )


                //adapterView.getItemAtPosition(position) as String 20/9/2021 de 11:00 h a 14:00 h

                //de 11:00 h a 14:00 h

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // vacio
            }
        }

        binding.toolbar.imageBack.setOnClickListener {
            getFragmentManager()?.popBackStackImmediate()
        }


        binding.acceptButton.setOnClickListener {

            val fragment = TramitarPedidoFragment()
            fragment.arguments = currentTimeBundle
            replaceFragment(fragment)
        }

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

    private fun goToCartScreen() {
        //viewModel.getTotalItens()
        startActivity(Intent(requireActivity(), CartActivity::class.java))

    }

    private fun loadCart(currentTime: String): List<CartItem>{
        val list: MutableList<CartItem> = mutableListOf()
        val cartItems = viewModelCart.authStore.getCart().map {
            list.add(
                CartItem(
                    qty = it.qty,
                    productId = it.productId,
                    product = it.product,
                    combinationReference = it.combinationReference,
                    combinationPrice = it.combinationPrice,
                    deliveredTypeOne = it.deliveredTypeOne,
                    currentTimeDelivered = it.currentTimeDelivered,
                    stock = it.stock,
                    type = it.type
                )
            )
        }

        var currentTimeDelivered = "22/09/2021"
        println(" currentTime tipo envio $currentTime")
        currentTimeDelivered = currentTime

        val cartItemsTest = viewModelCart.authStore.getCart().toMutableList()
        val newList = cartItemsTest.map { item ->
            viewModelCart.authStore.setCurrentTimeDelivered(currentTimeDelivered)
            //return@map item.copy(currentTimeDelivered = currentTimeDelivered)
            return@map item
        }

        return list
    }

    private fun deliveryType(deliveryType: String): List<CartItem>{

        val cartItemsTest = viewModelCart.authStore.getCart().toMutableList()
        cartItemsTest.map { item ->
            viewModelCart.authStore.setDeliveredType(deliveredType = deliveryType)
            return@map item
        }

        return cartItemsTest
    }

}