package com.miscota.android.ui.cart

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.adyen.checkout.card.CardValidationUtils
import com.adyen.checkout.card.data.CardType
import com.adyen.checkout.card.data.ExpiryDate
import com.adyen.checkout.cse.Card
import com.adyen.checkout.cse.Encryptor
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.google.firebase.perf.ktx.performance
import com.miscota.android.BuildConfig
import com.miscota.android.R
import com.miscota.android.address.AddressActivity
import com.miscota.android.addressold.AddressActivityOld
import com.miscota.android.auth.AuthActivity
import com.miscota.android.databinding.ActivityCartBinding
import com.miscota.android.events.EventsInfo
import com.miscota.android.ui.checkoutpayment.*
import com.miscota.android.ui.pedido.Pedido
import com.miscota.android.ui.pedido.PedidoNoProcesado
import com.miscota.android.ui.tramitarpedido.TramitarPedidoFragment
import com.miscota.android.util.Address
import org.koin.android.ext.android.bind
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding

    private val viewModel by viewModel<CartViewModel>()

    private lateinit var listAdapter: CartItemAdapter

    private val PUBLIC_KEY = "10001|E56D388FC118357D813AC3B5FC1AB56E9E9924130D5490F81ECB3C4E4B148128CBB56D0C9265C826309C389DC804660D5A86CE19065230F3801A2ABD196E19B1F7F499B439AE525290EA73F7A65AAB89F7DC3D56A286647CC668DA6585A6F9ECFCBF7630C60551894F8309BB0E9A76E88ACC5199881058EB6F89C00BA9E5DC099ED96F0425853B3DD1032B461198F705C5291AE664E4FD2E151F81B85F8929C805387AE6608119E8C4741783377ABE1FF6F68A320C603E3723D657019E6623D0C020AB33D1F472C4E90198920ACFAE3BEE33CEE6F5FDA30B62F738B892C60A2A6B0F620FB29450869A889B578F21821628DABFC9263F2584C71421B04784DDA5"
    private var paymentMethod: PaymentMethod? = null
    private var paymentResult = false

    private var costdelivered: Double = 0.0

    private var totalCartTens: Int? = null
    private var listMoreInfo: MutableList<CartUiModel.MoreInfoCheckout> = mutableListOf()

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = Firebase.analytics


        binding.toolbar.imageBack.setOnClickListener {
            finish()
        }

        binding.toolbar.cartItemsText.visibility = View.INVISIBLE
        binding.toolbar.storeImage.visibility = View.INVISIBLE
        //binding.toolbar.cartItemsText.text = viewModel.getTotalItens().toString()


        //val list = loadCheckout()
         viewModel.loadCartToList()
        var userType = false
        if (isLoggedIn())
        else
            userType = true

        //viewModel.carriersCheckout(userType, list)

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
                        totalCartTens = totalCartTens?.plus(1)
                    },
                    removeItemClickListener = { cartItem ->
                        viewModel.setQuantityRef(
                            id = cartItem.productId,
                            quantity = cartItem.quantity,
                            ref = cartItem.reference,
                            stock = cartItem.stock,
                            type = cartItem.type?: getString(R.string.type_ecommerce)
                        )
                       totalCartTens = totalCartTens?.minus(1)

                    },
                    deleteItemClickListener = { cartItem ->
                        //showDeleteConfirmationDialogRef(ref = cartItem.reference, userType, cartItem)

                        /**val swipeHandler = object : SwipeToDeleteCallback(this) {
                            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                                val adapter = binding.recyclerView.adapter as CartItemAdapter**/
                                //adapter.removeAt(viewHolder.adapterPosition)
                               // showDeleteConfirmationDialogRef(ref = cartItem.reference, userType, cartItem)
                        cartItem.type?.let { viewModel.removeItemRef(ref = cartItem.reference, type = it,this) }

                         /**       }
                            }

                        val itemTouchHelper = ItemTouchHelper(swipeHandler)
                        itemTouchHelper.attachToRecyclerView(binding.recyclerView)**/

                    },
                    changeAddress = {
                            showChangeAddressConfirmationDialog()
                    },
                    userLogued = viewModel.authStore.getUser()?.name,
                    isLogued = viewModel.authStore.isLoggedIn(),
                    userZone = {
                            startActivity(Intent(this, AuthActivity::class.java))
                    },
                    deliveredTypeClickListener = { cartItem ->
                        cartItem.deliveredTypeOne?.let { it1 ->
                            viewModel.authStore.setDeliveredType(
                                deliveredType = it1
                            )
                        }
                    },
                    currentTimeDelivered = { cartItem ->
                        cartItem.deliveredTypeOne?.let { it1 ->
                            viewModel.authStore.setCurrentTimeDelivered(
                                currentTimeDelivered = it1
                            )
                        }
                    },
                    type = { cartItem -> cartItem.type },
                    authStore = viewModel.authStore,
                    carriers =  { cartItem ->
                        cartItem.costEco.let {
                            viewModel.setCostEcoValue(viewModel.costEcommerce.value?:0.0)
                        }
                    },
                    carriersSd = { cartItem ->
                        cartItem.costSd.let {
                            viewModel.setCostSDValue(viewModel.costSd.value?:0.0)
                        }
                    },
                    types = arrayListOf(""),
                    payment = { cartItem ->
                        cartItem.payment?.let {
                            viewModel.setPayment(
                                encryptedCard = it.encryptedCardNumber,
                                encryptedDate = cartItem.payment.encryptedExpiryMonth,
                                encryptedYear = cartItem.payment.encryptedExpiryYear,
                                encryptedSecurityCode =  cartItem.payment.encryptedSecurityCode,
                                encryptedName = cartItem.payment.encryptedUserName
                            )
                        }
                    },
                    deliveredNextDay = {
                        showDeliveryConfirmationDialog()
                    },
                    carriersItem = {viewModel.setCostEcoValue(viewModel.costEcommerce.value?:0.0)},
                    carriersSdItem = { cartItem ->
                        cartItem.costSd.let {
                            viewModel.costSd.value
                        }
                    }
                )


      /**  val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding.recyclerView.adapter as CartItemAdapter
                //adapter.removeAt(viewHolder.adapterPosition)
                var item = adapter.getItemViewType(viewHolder.adapterPosition)
                adapter.getItemId(viewHolder.adapterPosition)

                //adapter.currentList.removeAt(viewHolder.adapterPosition)
                //showDeleteConfirmationDialogRef(ref = cartItem.reference, userType, cartItem)
                adapter.currentList.map { it
                    println("vc it $it ")
//                    adapter.deleteItemClickListener.invoke(it as CartUiModel.Item)
                }



                for(item in adapter.currentList){
                    println(" item $item")

                    }


            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)**/


        //firebase record view checkout
        //recordScreenViewCheckout()

        //firebase performance
        val myTrace = Firebase.performance.newTrace("CartActivity")
        myTrace.start()
        myTrace.stop()

        //load itens
        val listens = loadCheckout()
        var sum = 0
        listens.map {
            it.qty
            sum += it.qty.toInt()

        }

        totalCartTens = sum
        viewModel.authStore.setTotalCartItens(totalCartTens!!)

        //reference order
        viewModel.refOrder.observe(this){
            if (viewModel.refOrder.value!== null){
                return@observe
            }
        }

        //type delivered
        viewModel.types.observe(this){
            return@observe
        }

        //total carriers
        viewModel.total.observe(this) {
            binding.priceText.text = "${String.format("%.2f",it)} €"

            if(it == 0.0){
                //binding.checkoutButtonNew.visibility = View.GONE
                binding.checkoutButtonNew.isEnabled = false
                binding.checkoutButtonNew.text = "No tienes productos en el carrito"
            }
        }


        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = listAdapter
        }

        viewModel.items.observe(
            this,
              { list ->
                   listAdapter.submitList(list)

                     if (list.any()) {
                            binding.emptyView.visibility = View.GONE
                            binding.constraintLayout.visibility = View.VISIBLE

                     } else {
                            binding.emptyView.visibility = View.VISIBLE
                            binding.constraintLayout.visibility = View.GONE
                     }

              }
        )

        viewModel.messageEvent.observe(
            this,
              {
                 it.consume()?.let { message ->
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                 }
              }
        )

        binding.emptyView.layoutParams.height = 0

        viewModel.costDeliver.observe(this){
            costdelivered = viewModel.costDeliver.value!!
            return@observe
        }

        viewModel.payment.observe(this){
                println(" pay ${viewModel.payment.value}")
                return@observe
            }

        binding.checkoutButtonNew.setOnClickListener {

            //Navigation.createNavigateOnClickListener(R.id.tramitarPedidoFragment, null)

            val pedido = viewModel.checkoutResult.value
            val resultOk = viewModel.refOrder.value
            val bundleEcommerce = Bundle()
            bundleEcommerce.putString("resultOk", resultOk)
            bundleEcommerce.putString("pedido", pedido.toString())

            val fm: FragmentManager = supportFragmentManager
            val ft: FragmentTransaction = fm.beginTransaction()

            val fragment = TramitarPedidoFragment()
            ft.add(R.id.thankyou, TramitarPedidoFragment())
            fragment.arguments = bundleEcommerce
            ft.commit()


           /** val pendingIntent = NavDeepLinkBuilder(this.applicationContext)
                .setGraph(R.navigation.mobile_navigation)
                .setDestination(R.id.tramitarPedidoFragment)
                .createPendingIntent()

            pendingIntent.send()**/


        }


        binding.checkoutButton.setOnClickListener {

                    recordCheckoutClick()

                    val cartItems: LiveData<List<CartUiModel>> = viewModel.items
                    cartItems.observe(this) {
                        println("\n viewModel.items $it \n")
                    }

                    listAdapter.hasObservers()

                    viewModel.authStore.getCart().map {
                        println("  viewModel.authStore.getCart().map it $it \n")


                        if (it.product.stockItens == 0) {
                            Toast.makeText(
                                this,
                                "De este producto: \n ${it.product.title} ${it.productId}no tenemos stock \n Lo sentimos mucho",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                        if (it.stock == 0) {
                            Toast.makeText(
                                this,
                                "De este producto: \n ${it.product.title} ${it.productId}no tenemos stock \n Lo sentimos mucho",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    }

                    //test
                    /**paymentMethod =
                    encrypt(viewModel.payment.value!!.encryptedCardNumber,
                    viewModel.payment.value!!.encryptedExpiryMonth.substring(0,2),
                    viewModel.payment.value!!.encryptedExpiryYear.substring(3,5),
                    viewModel.payment.value!!.encryptedSecurityCode,
                    "owner")**/

                    val listCheckoutProducts = loadCheckout()
                    var userType = false
                    if (!isLoggedIn())
                    else
                        userType = true

                   val listAddress:  List<Address> = listOf(Address("","",0.0,0.0,"","","","","","","","",""))

                    //paymentMethod = encrypt("4988438843884305","03","30","737", "Fernanda")
                    val checkoutInit = beforeCheckout(
                        listCheckoutProducts,
                        listMoreInfo,
                        viewModel.authStore.getAddress()?: Address("","",0.0,0.0,"","","","","","","","",""),
                        viewModel.authStore.getRecentAddresses()?: listAddress,
                        viewModel.authStore.getAddressInfo()?:Address("","",0.0,0.0,"","","","","","","","",""),
                        viewModel.authStore.getEmail()?: " ",
                    )

                if (checkoutInit){
                    //firebase analytics begin checkout
                    listCheckoutProducts.map {
                       val item = viewModel.eventsManager.itemCheckout(it)
                                  viewModel.eventsManager.beginCheckout(item, getTotalPriceAnalytics())
                    }

                    //test
                    //paymentMethod = encrypt("4988438843884305","03","30","737", "Fernanda")
                    //pro
                    //paymentMethod = encrypt("5566710637979013","02","23","079", "Xavier Blanch")

                   paymentMethod =
                        encrypt(
                            viewModel.payment.value?.encryptedCardNumber?:"4000",
                            viewModel.payment.value?.encryptedExpiryMonth?.substring(0, 2)?:"0",
                            viewModel.payment.value?.encryptedExpiryYear?.substring(3, 5)?:"0",
                            viewModel.payment.value?.encryptedSecurityCode?:"0",
                            "owner"
                        )

                    val payment = paymentMethod


                    viewModel.checkout(userType, listCheckoutProducts, costdelivered, currentTime = "DEFAULT", deliveryType = "DEFAULT_TYPE")
                    val refOrder = ""

                    viewModel.refOrder.observe(this) {
                        if (viewModel.refOrder.value != null) {
                            //println(" empieza el pago.....del pedido.${viewModel.refOrder.value!!}")
                            listCheckoutProducts.map {
                                //firebase analytics shipping info
                                val item = viewModel.eventsManager.itemCheckout(it)
                                viewModel.eventsManager.shippingInfo(it,viewModel.refOrder.value!!,item)
                            }

                            val paymentMethod = PaymenthMethodsAdyen(
                                amount =
                                Amount(currency = "EUR", value = getTotalPriceAdyen()),
                                merchantAccount = "MiscotaCOM",
                                reference = viewModel.refOrder.value!!
                            )

                            listAdapter.currentList.subList(0, 1).map {
                                println(" it totalI  $it")
                            }


                            val paymentRequest = PaymentRequestAdyen(
                                amount =
                                Amount(currency = "EUR", value = getTotalPriceAdyen()),
                                merchantAccount = BuildConfig.MERCHANT_ACCOUNT_MIS,
                                paymentMethod = payment!!,
                                reference = viewModel.refOrder.value!!,
                                returnUrl = "miscota://paymentResult"
                            )
                            //viewModel.fetchPaymentTest(paymentRequest)
                            viewModel.fetchPaymentPro(paymentRequest)

                            viewModel.checkoutResult.observe(this) {
                                var pedido = viewModel.checkoutResult.value
                                val resultOk = viewModel.refOrder.value
                                val bundleEcommerce = Bundle()
                                bundleEcommerce.putString("resultOk", resultOk)
                                if (paymentResult) {

                                    val fm: FragmentManager = supportFragmentManager
                                    val ft: FragmentTransaction = fm.beginTransaction()

                                    val fragment = Pedido()
                                    ft.add(R.id.thankyou, Pedido())
                                    fragment.arguments = bundleEcommerce
                                    ft.commit()



                                    val screenName = "checkout_finished_ok"
                                    val eventsInfo: EventsInfo
                                        = EventsInfo( screenName = screenName,
                                    methodName = "fetchPaymentPro",
                                    screenClass = "CartActivity")

                                    //cartItem: CartItem, order: String, item: Bundle, cartShip: Double, total: Double, eventsInfo: EventsInfo
                                    //viewModel.eventsManager.purchase(arrayOf(list),)

                                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.PURCHASE) {
                                        param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
                                        param(FirebaseAnalytics.Param.SCREEN_CLASS, "CartActivity")
                                        param(FirebaseAnalytics.Param.TRANSACTION_ID, viewModel.refOrder.value!!)
                                        param(FirebaseAnalytics.Param.AFFILIATION, "Miscota App Android")
                                        param(FirebaseAnalytics.Param.VALUE,getTotalPriceAnalytics())
                                        param(FirebaseAnalytics.Param.CURRENCY, "EUR")
                                        param(FirebaseAnalytics.Param.METHOD, "fetchPaymentPro")
                                    }

                                    //vaciar carrito
                                    listCheckoutProducts.map {
                                        viewModel.removeItemRef(it.ref, it.type?: getString(R.string.type_ecommerce),this)
                                    }


                                } else if (!paymentResult) {
                                    val fm: FragmentManager = supportFragmentManager
                                    val ft: FragmentTransaction = fm.beginTransaction()

                                    var fragment = PedidoNoProcesado()
                                    ft.add(R.id.thankyou, PedidoNoProcesado())
                                    //fragment.arguments = bundleEcommerce
                                    ft.commit()
                                }

                            }

                        }
                        return@observe
                    }
                    println(" ref-order after observe ${viewModel.refOrder.value}")
                    println(" ref-order refOrder $refOrder")
                }else if( !checkoutInit ){
                    val fm: FragmentManager = supportFragmentManager
                    val ft: FragmentTransaction = fm.beginTransaction()

                    var fragment = PedidoNoProcesado()
                    ft.add(R.id.thankyou, PedidoNoProcesado())
                    //fragment.arguments = bundleEcommerce
                    ft.commit()

                }
                    listAdapter.currentList.size
                    listAdapter.currentList.map {
                        println("\n listAdapter.currentList.map it $it")
                    }


                    println("\n Subtotal y uid unico para cada venta ${listAdapter.currentList.get(0)}")
                    println("\n SummaryItem = direction, time ${listAdapter.currentList.get(listAdapter.currentList.size-1)}")

                    for (item in listAdapter.currentList)
                        println(" \n for item listable $item")
                    println(" \n for item cartItems.value ${cartItems.value?.get(0)}")

                }

        viewModel.checkoutResult.observe(this) {
            val checkoutResult = it ?: return@observe

            if (checkoutResult.error != null) {
                showLoginFailed(checkoutResult.error)
            }
            /**else if (checkoutResult.success != null) {
                //showCheckoutSuccess()
            }**/
        }

    }



    private fun recordScreenViewCheckout() {
        val screenName = "screen_checkout"

        firebaseAnalytics.logEvent(screenName) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, "CartActivity")
        }
    }

    private fun recordCheckoutClick() {
        // This string must be <= 36 characters long.
        val screenName = "screen_checkout_buy_now_click"

        firebaseAnalytics.logEvent(screenName) {
            param(FirebaseAnalytics.Param.METHOD, "checkoutButton")
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, "CartActivity")
        }
    }


    //total price to adyen platform
    private fun getTotalPriceAdyen(): Int{
        val price = binding.priceText.text.toString().split(" ").toTypedArray()
        var allPrice = price[0].replace(".","")
        allPrice = price[0].replace(",","")
        val priceToAdyen = allPrice.toInt()
        println(" proceeded ${price[0].replace(".","")}")
        return priceToAdyen
    }

    //total price to firebase analytics purchase
    private fun getTotalPriceAnalytics(): Double{
        val price = binding.priceText.text.toString().split(" ").toTypedArray()
        var allPrice = price[0].replace(".","")
        allPrice = price[0].replace(",","")
        val priceAnalytics = price[0].replace(",",".")

        return priceAnalytics.toDouble()
    }

    //total carriers
    private fun loadCarriers(costSd: Double, costEcommerce: Double): Double{
        val carrier = (costSd + costEcommerce)
            println("viewModel.costEcommerce.value loadCarriers line 521 CartActivity ${viewModel.costEcommerce.value}")
            println("viewModel.costSd.value ${viewModel.costSd.value}")

        println(" carrier $carrier")
        return carrier
    }

    //load checkout
    fun loadCheckout(): MutableList<CartUiModel.ItemListCheckout>{

        val list: MutableList<CartUiModel.ItemListCheckout> = mutableListOf()
         viewModel.authStore.getCart().map {
            it.toCartItemUiModel()
            println(" it items checkout: $it")
            list.add(
                CartUiModel.ItemListCheckout(
                    qty = it.qty.toString(),
                    price = it.combinationPrice.toString(),
                    type = it.type,
                    ref = it.combinationReference,
                )
            )

            //firebase analytics Event viewCart
            val itemCart = viewModel.eventsManager.viewItemCart(it)
                            viewModel.eventsManager.viewCart(it, itemCart)
            //endfirebase analytics

            listMoreInfo.add(
                CartUiModel.MoreInfoCheckout(
                guess = if (viewModel.authStore.getUser() != null) false else true,
                stock = it.stock?:1,
                ref = it.combinationReference,
                rangeDelivery = "RangeDelivered",
                productType = it.type!!
                )
            )

        }

        list.map {
            println(" it list items cart:  $it")

        }
        return list
    }

    //show messages
    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(this, errorString, Toast.LENGTH_SHORT).show()
    }
    private fun showCheckoutSuccess() {
        Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
    }

    //remove all itens from cart if is sameday
    private fun removeItemsSameday(){
        val listItens = loadCheckout()
        listItens.map {
            if(it.type == "sameday")
            viewModel.removeItemRef(it.ref, it.type,this)
        }

    }


    override fun onStart() {
        super.onStart()
        viewModel.loadCart()

        viewModel.costEcommerce.observe(this){
            if (viewModel.costEcommerce.value!== null){
                costdelivered = (viewModel.costSd.value!! + viewModel.costEcommerce.value!!)
                viewModel.loadCarriers()
                return@observe
            }
        }

        viewModel.costSd.observe(this){
            listAdapter.notifyDataSetChanged()
            if (viewModel.costSd.value!== null){
                costdelivered = loadCarriers(viewModel.costSd.value!!, viewModel.costEcommerce.value!!)
                println(" misdelivered sd $costdelivered")
                println(" viewModel.costSd.value ${viewModel.costSd.value}")
                return@observe
            }
        }



        //applicationContext
        // (requireActivity() as MainActivity).samedayInfoBottom.visibility = View.VISIBLE

        //(applicationContext as MainActivity).binding.imageBack.isVisible = false

        //applicationContext.MainActivity.binding.samedayInfoBottom.visibility = View.VISIBLE

        //(MainActivity)requi.getActivity()


    }



    private fun showDeleteConfirmationDialog(id: Int) {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.remove_item_heading))
            .setMessage(getString(R.string.remove_item_msg))
            .setPositiveButton(getString(R.string.yes)) { dialogInterface, _ ->
                dialogInterface.dismiss()
                viewModel.removeItem(id, this)
            }
            .setNegativeButton(getString(R.string.no)) { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun showDeleteConfirmationDialogRef(ref: String, type: String, userType: Boolean, itemCarts: CartUiModel.Item) {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.remove_item_heading))
            .setMessage(getString(R.string.remove_item_msg))
            .setPositiveButton(getString(R.string.yes)) { dialogInterface, _ ->
                dialogInterface.dismiss()
                viewModel.removeItemRef(ref, type, this)

                //firebase analytics Event removeFromCart
                val itemCart = viewModel.eventsManager.itemRemoveToCart(itemCarts)
                viewModel.eventsManager.removeFromCart(itemCart, itemCarts, itemCarts.quantity)
            }
            .setNegativeButton(getString(R.string.no)) { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun showDeliveryConfirmationDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.next_day_tem_heading))
            .setMessage(getString(R.string.next_day_item_msg))
            .setPositiveButton(getString(R.string.next_day_yes)) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun showChangeAddressConfirmationDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.change_address_tem_heading))
            .setMessage(getString(R.string.change_address_item_msg))
            .setPositiveButton(getString(R.string.yes)) { dialogInterface, _ ->
                dialogInterface.dismiss()
                removeItemsSameday()
                if (isConnected()) {
                    startActivity(Intent(this, AddressActivityOld::class.java))
                }else{
                    Toast.makeText(this,R.string.message_conected, Toast.LENGTH_LONG).show()
                }
            }
            .setNegativeButton(getString(R.string.no)) { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            .show()
    }


    fun isLoggedIn(): Boolean {
        return viewModel.authStore.isLoggedIn()
    }


    private fun encrypt(
        cardNumber: String,
        expireDate: String,
        expireYear: String,
        securityCode: String,
        ownerName: String
    ): PaymentMethod {

        val cardNumber = cardNumber
        val ownerName = "ownerName"
        val expire = "20$expireYear"
        val cardType =
            CardType.estimate(cardNumber)[0] // This is just an estimation and could be empty
        val expiryDate = ExpiryDate(expireDate.toInt(), expire.toInt())
        val securityCode = securityCode

        if (CardValidationUtils.validateCardNumber(cardNumber).isValid
            && CardValidationUtils.validateExpiryDate(expiryDate).isValid
            && CardValidationUtils.validateSecurityCode(securityCode, cardType).isValid
        ) {
            val rawCardData = Card.Builder()
                .setNumber(cardNumber)
                .setExpiryDate(expiryDate.expiryMonth, expiryDate.expiryYear)
                .setSecurityCode(securityCode)
                .build()
            //println(" rawCardData $rawCardData")

            val encryptedCard = Encryptor.INSTANCE.encryptFields(
                rawCardData,
                PUBLIC_KEY
                //BuildConfig.PUBLIC_KEY_MIS
            )

            paymentMethod = PaymentMethod(
                encryptedCardNumber = encryptedCard.encryptedNumber.toString(),
                encryptedExpiryMonth = encryptedCard.encryptedExpiryMonth.toString(),
                encryptedExpiryYear = encryptedCard.encryptedExpiryYear.toString(),
                encryptedSecurityCode = encryptedCard.encryptedSecurityCode.toString(),
                encryptedUserName = "user name default"
            )
            println(" target encrypted \n $encryptedCard")
            println(" target encrypted.encryptedExpiryMonth \n ${encryptedCard.encryptedExpiryMonth}")
            println(" target encrypted.encryptedExpiryYear \n ${encryptedCard.encryptedExpiryYear}")
            println(" target encrypted.encryptedNumber \n ${encryptedCard.encryptedNumber}")
            println(" target encrypted.encryptedSecurityCode \n ${encryptedCard.encryptedSecurityCode}")
            paymentResult = true

        }else{
            Toast.makeText(this,"Tarjeta inválida",Toast.LENGTH_SHORT).show()
            return PaymentMethod(
                encryptedCardNumber = "0",
                encryptedExpiryMonth =  "0",
                encryptedExpiryYear =  "0",
                encryptedSecurityCode =  "0",
                encryptedUserName = "user name default")
            paymentResult = false

        }

        return paymentMethod!!

    }

    private fun beforeCheckout(
        list: MutableList<CartUiModel.ItemListCheckout>,
        listInfo: MutableList<CartUiModel.MoreInfoCheckout>,
        address: Address,
        addressSecond: List<Address>,
        addressThird: Address,
        email: String
    ): Boolean{

        var checkoutStock = true
        var checkoutItems = true
        var checkoutUser = true
        var checkoutAddress = true
        //var payment = true

        if(list.size == 0){
            val mensaje = "Debe tener al menos un producto en el carrito"
            checkoutItems = false
        }
        else if ((addressThird.postalCode == null && addressThird.address == null &&
                addressThird.city == null && addressThird.region == null ))
        {
            Toast.makeText(this, "Por favor añada tu dirección completa " +
                    "\n para que podamos entregar correctamente tu pedido",
                Toast.LENGTH_LONG).show()

            startActivity(Intent(this, AddressActivity::class.java))
            checkoutAddress = false
        }
        else if(!viewModel.authStore.isLoggedIn() && viewModel.authStore.getUser() == null){

            Toast.makeText(this, "Faltan datos para completar tu compra",
                Toast.LENGTH_LONG).show()
            checkoutUser = false
        }


        list.map {
            it.qty
            it.ref
            listInfo.map { it2 ->
                it2.ref
                it2.stock

                checkoutStock = it.ref == it2.ref && it.qty.toInt() <= it2.stock

            }

        }
        return !(!checkoutAddress || !checkoutItems || !checkoutStock || !checkoutUser )
    }

    fun isConnected(): Boolean {
        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

    override fun onResume() {
        super.onResume()
        viewModel.costSd.observe(this){
            return@observe
        }
        viewModel.costEcommerce.observe(this){
            return@observe
        }

    }


}