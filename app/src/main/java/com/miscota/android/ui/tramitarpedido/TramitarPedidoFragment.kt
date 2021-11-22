package com.miscota.android.ui.tramitarpedido

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.Toast
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.adyen.checkout.card.CardValidationUtils
import com.adyen.checkout.card.data.CardType
import com.adyen.checkout.card.data.ExpiryDate
import com.adyen.checkout.cse.Card
import com.adyen.checkout.cse.Encryptor
import com.google.firebase.analytics.FirebaseAnalytics
import com.miscota.android.BuildConfig
import com.miscota.android.R
import com.miscota.android.address.AddressActivity
import com.miscota.android.auth.AuthActivity
import com.miscota.android.databinding.TramitarPedidoFragmentBinding
import com.miscota.android.events.EventsInfo
import com.miscota.android.ui.addresscurrent.AddressCurrentFragment
import com.miscota.android.ui.cart.*
import com.miscota.android.ui.checkoutpayment.Amount
import com.miscota.android.ui.checkoutpayment.PaymentMethod
import com.miscota.android.ui.checkoutpayment.PaymentRequestAdyen
import com.miscota.android.ui.checkoutpayment.PaymenthMethodsAdyen
import com.miscota.android.ui.discount.DiscountFragment
import com.miscota.android.ui.paymentmethod.PaymentMethodFragment
import com.miscota.android.ui.pedido.Pedido
import com.miscota.android.ui.tipodeenvio.TipoEnvioFragment
import com.miscota.android.util.Address
import com.miscota.android.util.CardN
import com.miscota.android.util.CartItem
import com.miscota.android.util.autoClean
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class TramitarPedidoFragment : Fragment() {

    companion object {
        fun newInstance() = TramitarPedidoFragment()
        const val MAX_LENGTH_CARD = 16
        const val CURRENT_ZERO_DEFAULT = "0"
    }

    private lateinit var viewModel: TramitarPedidoViewModel
    private val viewModelCart by viewModel<CartViewModel>()

    private val PUBLIC_KEY = "10001|E56D388FC118357D813AC3B5FC1AB56E9E9924130D5490F81ECB3C4E4B148128CBB56D0C9265C826309C389DC804660D5A86CE19065230F3801A2ABD196E19B1F7F499B439AE525290EA73F7A65AAB89F7DC3D56A286647CC668DA6585A6F9ECFCBF7630C60551894F8309BB0E9A76E88ACC5199881058EB6F89C00BA9E5DC099ED96F0425853B3DD1032B461198F705C5291AE664E4FD2E151F81B85F8929C805387AE6608119E8C4741783377ABE1FF6F68A320C603E3723D657019E6623D0C020AB33D1F472C4E90198920ACFAE3BEE33CEE6F5FDA30B62F738B892C60A2A6B0F620FB29450869A889B578F21821628DABFC9263F2584C71421B04784DDA5"


    private var binding by autoClean<TramitarPedidoFragmentBinding>()

    private lateinit var listAdapter: TramitarPedidoImageAdapter

    private lateinit var recentAddresses: List<Address>

    lateinit var recentAddressesUser: List<Address>

    private var addressUser: Address? = null

    private var addressUserInfo: Address? = null

    private var paymentMethod: PaymentMethod? = null
    private var paymentResult = false

    private var costdelivered: Double = 0.0

    private var totalCartTens: Int? = null
    private var listMoreInfo: MutableList<CartUiModel.MoreInfoCheckout> = mutableListOf()

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    lateinit var gridView: GridView

    private var carriersSd: Double = 0.0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TramitarPedidoFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val clientType = isLogued()
        val items = loadCheckout()

        carriersSd = viewModelCart.carriersCostSd(clientType, items)

        loadRecentAddresses()
        loadAddressesUser()
        loadAddressUser()
        loadAddressInfo()
        loadCartToItem()
        //START TO TEST - CARD TEST
        /**val card = CardN(card="4988438843884305", security= "737",expireYear= "03/30".split("/")[1], expireMonth="03/30".split("/").first(), owner="Persona")
        viewModelCart.authStore.setCard(card)**/
        //END TO TEST

        val bundlePayment: Bundle? = arguments
        val currentTimeBundle: Bundle? = arguments
        val cardOwner: String? = bundlePayment?.getString(getString(R.string.card_user_encrypt))
        val cardNumber: String? = bundlePayment?.getString(getString(R.string.card_number_encrypt))
        val cardSecurity: String? = bundlePayment?.getString(getString(R.string.card_security_encrypt))
        val cardYear: String? = bundlePayment?.getString(getString(R.string.card_expire_year_encrypt))
        val cardMonth: String? = bundlePayment?.getString(getString(R.string.card_expire_encrypt))

        val bundleAddress: Bundle? = arguments
        val addressB: String? = bundleAddress?.getString("addressB")
        val provinceB: String? = bundleAddress?.getString("provinceB")
        val postalCodeB: String? = bundleAddress?.getString("postalCodeB")
        val cityB: String? = bundleAddress?.getString("cityB")
        val phoneB: String? = bundleAddress?.getString("phoneB")

        val bundleAddressCurrent: Bundle? = arguments
        val addressBC: String? = bundleAddressCurrent?.getString("addressBC")
        val provinceBC: String? = bundleAddressCurrent?.getString("provinceBC")
        val postalCodeBC: String? = bundleAddressCurrent?.getString("postalCodeBC")
        val cityBC: String? = bundleAddressCurrent?.getString("cityBC")
        val phoneBC: String? = bundleAddressCurrent?.getString("phoneBC")


        println("addressB $addressB")
        println("addressBC $addressBC")
        if( cardNumber != null && cardMonth != null && cardYear !=  null && cardSecurity != null && cardNumber.isNotEmpty()) {
            paymentMethod = encrypt(cardNumber, cardMonth.split("/").first(),
                cardYear.split("/")[1], cardSecurity, cardOwner?:"OwnerNameDefault")

            val card = CardN(card=cardNumber, security= cardSecurity,expireYear= cardYear.split("/")[1], expireMonth=cardMonth.split("/").first(), owner=cardOwner)
            viewModelCart.authStore.setCard(card)

        }

        viewModelCart.costSd.observe(requireActivity()){
            carriersSd = it
            binding.sameDayPrice.text = String.format("${it.toDouble()}"+" €")
            return@observe
        }

        viewModelCart.costEcommerce.observe(requireActivity()){
            binding.ecommercePrice.text = String.format("${it.toDouble()}"+" €")
            return@observe
        }

        val subtotal: Double = viewModelCart.authStore.getCart().map {
            it.combinationPrice * it.qty
        }.foldRight(0.0) { acc, d -> acc + d }

        var totalDel: Double
        viewModelCart.costSd.observe(requireActivity()){
            totalDel = it.toDouble() + (viewModelCart.authStore.getCarriersEco()
                ?.toDouble() ?:0.0)
 
            val totalCheckout = subtotal + totalDel
            binding.totalPrice.text = String.format(String.format("%.2f", totalCheckout)+" €")
        }
        
        binding.productsPrice.text = String.format(String.format("%.2f", subtotal)+" €")

        if (viewModelCart.authStore.getCard() != null){

            binding.creditCardNumber.text = String.format(resources.getString(R.string.card_mask), viewModelCart.authStore.getCard()!!.card.substring(12,MAX_LENGTH_CARD))
            binding.creditCardName.text = viewModelCart.authStore.getCard()!!.owner.toString()
        }
        if (viewModelCart.authStore.getCard() == null){

            binding.creditCardTitle.text = getString(R.string.card_number_message)
            binding.creditCardNumber.visibility = View.GONE
            binding.creditCardName.visibility = View.GONE

            val params = (binding.creditCardTitle.layoutParams as ViewGroup.MarginLayoutParams)
            params.setMargins(0, 90, 0, 20)
            binding.creditCardTitle.layoutParams = params

        }
        if( cardNumber != null ){

            binding.creditCardNumber.text = String.format(resources.getString(R.string.card_mask),cardNumber.substring(12,MAX_LENGTH_CARD))

        }
        if(cardOwner != null ){
            binding.creditCardName.text = cardOwner.toString()

        }

        if (addressB != null && provinceB != null && postalCodeB != null && cityB != null && phoneB != null){

            binding.addressShipping.text = addressB
            binding.addressShippingComplement.text = String.format("$postalCodeB, $cityB, $phoneB ,$provinceB, España")
            viewModelCart.authStore.setPhone(phoneB)
        }
        if (addressBC != null && provinceBC != null && postalCodeBC != null && cityBC != null && phoneBC != null){

            binding.addressShipping.text = addressBC
            binding.addressShippingComplement.text = String.format("$postalCodeBC, $cityBC, $phoneBC ,$provinceBC, España")
            viewModelCart.authStore.setPhone(phoneBC)

        }
        if (addressUser?.addressNumber != null){
            println("11")
            if(addressUser!!.addressNumber.length > 1) {
                println("111")
                binding.addressShipping.text = addressUser?.addressNumber
                binding.addressShippingComplement.text =
                    String.format("${addressUser?.postalCode}, ${addressUser?.city}, ${addressUser?.state} ,${addressUser?.countryName}")
            }
        }
        else {
            if (addressUserInfo?.addressNumber != null ){
                println("22")
                if(addressUserInfo!!.addressNumber.length > 1) {
                    println("222")
                    binding.addressShipping.text = addressUserInfo?.addressNumber
                    binding.addressShippingComplement.text =
                        String.format("${addressUserInfo?.postalCode}, ${addressUserInfo?.city}, ${addressUserInfo?.state} ,${addressUserInfo?.countryName}")
                }
            }else {
                println("33")
                if (recentAddressesUser.isNotEmpty() && recentAddressesUser != null) {
                    println("333")
                    binding.addressShipping.text = recentAddressesUser.first().addressNumber
                    binding.addressShippingComplement.text =
                        String.format("${recentAddressesUser.first().postalCode}, ${recentAddressesUser.first().city}, ${recentAddressesUser.first().state}, ${recentAddressesUser.first().countryName}")

                } else {
                    println("44")
                    if (recentAddresses.isNotEmpty() && recentAddresses != null) {
                        println("444")
                        recentAddresses.map {
                            it.address
                            binding.addressShipping.text = it.addressNumber
                            binding.addressShippingComplement.text =
                                String.format("${it.postalCode}, ${it.city}, ${it.state}, ${it.countryName}")
                        }
                    }
                }
            }
        }

        if(isLogued()){
            binding.clientName.text = "${viewModelCart.authStore.getUser()?.name}"

            if (viewModelCart.authStore.getUser()?.surname != null){
                binding.clientName.text = String.format("${viewModelCart.authStore.getUser()?.name}, ${viewModelCart.authStore.getUser()?.surname}")
            }
        }
        if(!isLogued()){
            binding.clientName.text = getString(R.string.need_login)
            val params = (binding.clientName.layoutParams as ViewGroup.MarginLayoutParams)
            params.setMargins(0, 90, 0, 20)
            binding.clientName.layoutParams = params

        }

        if(!isLogued()  && (addressUser?.addressNumber == null  || addressUserInfo?.addressNumber == null)){
            binding.clientName.text = getString(R.string.need_login)
            binding.addressShipping.visibility = View.GONE
            binding.addressShippingComplement.visibility = View.GONE

            val params = (binding.clientName.layoutParams as ViewGroup.MarginLayoutParams)
            params.setMargins(0, 90, 0, 20)
            binding.clientName.layoutParams = params

        }

        if(!isLogued()  && (addressUser?.addressNumber != null  || addressUserInfo?.addressNumber != null)){
            binding.clientName.text = getString(R.string.need_login)
            binding.addressShippingComplement.visibility = View.VISIBLE
            //binding.addressShipping.text = addressUser?.addressNumber?:addressUserInfo?.addressNumber
            binding.addressShippingComplement.text = "${addressUser?.postalCode}, ${addressUser?.city}, ${addressUser?.state} ,${addressUser?.countryName}"
                ?:"${addressUserInfo?.postalCode}, ${addressUserInfo?.city}, ${addressUserInfo?.state} ,${addressUserInfo?.countryName}"
            val params = (binding.clientName.layoutParams as ViewGroup.MarginLayoutParams)
            params.setMargins(0, 75, 0, 0)
            binding.clientName.layoutParams = params

        }

        val bundleCategory: Bundle? = arguments
        val pedido: String? = bundleCategory?.getString("resultOk")

        binding.toolbar.imageBack.setOnClickListener {
            val cartItemsTest = viewModelCart.authStore.getCart().toMutableList()
            val newList = cartItemsTest.map { item ->
                    //viewModelCart.authStore.setCurrentTimeDelivered("0")
                //return@map item.copy(currentTimeDelivered = currentTimeDelivered)
                return@map item
            }

            goToCartScreen()
            requireActivity().finish()

        }

        binding.productTotalCart.text = String.format("( ${viewModelCart.getTotalItens()} )")

        DrawableCompat.setTint(
            binding.loading.indeterminateDrawable,
            Color.parseColor("#4FC3F7")
        )

        viewModelCart.showLoading.observe(requireActivity()) {
            if (!viewModelCart.showLoading.value!!) {
                binding.loadingText.visibility = View.GONE
                binding.loadingLayout.visibility = View.GONE
                binding.loading.visibility = View.GONE

                binding.orderSummaryCard.visibility = View.VISIBLE
                binding.productsListCard.visibility = View.VISIBLE
                binding.typeOrderCard.visibility = View.VISIBLE
                binding.addressOrderCard.visibility = View.VISIBLE
                binding.paymentMethodCard.visibility = View.VISIBLE

            }
            if (viewModelCart.showLoading.value!!) {
                binding.loadingText.text = getString(R.string.loading_buy)
                binding.loadingText.visibility = View.VISIBLE
                binding.loadingLayout.visibility = View.VISIBLE
                binding.loading.visibility = View.VISIBLE

                binding.orderSummaryCard.visibility = View.INVISIBLE
                binding.productsListCard.visibility = View.INVISIBLE
                binding.typeOrderCard.visibility = View.INVISIBLE
                binding.addressOrderCard.visibility = View.INVISIBLE
                binding.paymentMethodCard.visibility = View.INVISIBLE

            }
        }

        binding.buyNowButtonCard.setOnClickListener {
            viewModelCart.showLoading.value = true
            val cartItems: LiveData<List<CartUiModel>> = viewModelCart.items
            cartItems.observe(requireActivity()) {

            }

            listAdapter.hasObservers()

            var deliveryType = getString(R.string.delivery_type_default)
            var currentTime = getString(R.string.current_time_default)

            viewModelCart.authStore.getCart().map {

                deliveryType = it.deliveredTypeOne
                currentTime = it.currentTimeDelivered.toString()

                if (it.product.stockItens == 0) {
                    Toast.makeText(
                        requireContext(),
                        "De este producto: \n ${it.product.title} ${it.productId}no tenemos stock \n Lo sentimos mucho",
                        Toast.LENGTH_SHORT
                    ).show()

                }
                if (it.stock == 0) {
                    Toast.makeText(
                        requireContext(),
                        "De este producto: \n ${it.product.title} ${it.productId}no tenemos stock \n Lo sentimos mucho",
                        Toast.LENGTH_SHORT
                    ).show()
                    viewModelCart.removeItemRef(it.combinationReference, it.type?:getString(R.string.type_ecommerce),requireContext())
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
            if (isLoggedIn())
                userType = false

            val listAddress:  List<Address> = listOf(Address("","",0.0,0.0,"","","","","","","","",""))



            //paymentMethod = encrypt("4988438843884305","03","30","737", "Fernanda")
            val checkoutInit = beforeCheckout(
                listCheckoutProducts,
                listMoreInfo,
                viewModelCart.authStore.getAddress()?: Address("","",0.0,0.0,"","","","","","","","",""),
                viewModelCart.authStore.getRecentAddresses()?: listAddress,
                viewModelCart.authStore.getAddressInfo()?:Address("","",0.0,0.0,"","","","","","","","",""),
                viewModelCart.authStore.getEmail()?: " ",
                cardNumber?:viewModelCart.authStore.getCard()?.card?:CURRENT_ZERO_DEFAULT,
                deliveryType,
                currentTime
            )




            if (checkoutInit){

                //firebase analytics begin checkout
                listCheckoutProducts.map {
                    val item = viewModelCart.eventsManager.itemCheckout(it)
                    viewModelCart.eventsManager.beginCheckout(item, getTotalPriceAnalytics())
                }

                //test
                //paymentMethod = encrypt("4988438843884305","03","30","737", "Fernanda")
                if( cardNumber != null && cardMonth != null && cardYear !=  null && cardSecurity != null) {

                    paymentMethod = encrypt(cardNumber, cardMonth.split("/").first(),
                        cardYear.split("/").get(1),
                        cardSecurity,
                        cardOwner?:"OwnerNameDefault")
                }
                if ( viewModelCart.authStore.getCard()?.card != null &&
                        viewModelCart.authStore.getCard()?.expireMonth != null &&
                        viewModelCart.authStore.getCard()?.expireYear != null &&
                        viewModelCart.authStore.getCard()?.security != null){

                    paymentMethod = encrypt(
                        viewModelCart.authStore.getCard()?.card!!,
                        viewModelCart.authStore.getCard()?.expireMonth!!,
                        viewModelCart.authStore.getCard()?.expireYear!!,
                        viewModelCart.authStore.getCard()?.security!!,
                        viewModelCart.authStore.getCard()?.owner!!)
                }
                //pro
                //paymentMethod = encrypt("5566710637979013","02","23","079", "Xavier Blanch")

                /**paymentMethod =
                    encrypt(
                        cardNumber?:"4000",
                        cardMonth?.substring(0, 2)?:"0",
                        cardYear?.substring(3, 5)?:"0",
                        cardSecurity?:"0",
                        cardOwner?:"owner"
                    )**/

                val payment = paymentMethod

               costdelivered = viewModelCart.authStore.getCarriersEco()?.toDouble() ?: 0.0.plus(viewModelCart.authStore.getCarriersSd()?.toDouble()?:0.0)

                //println("just before checkout currentTime ${currentTime.split("(").firstOrNull()}")

                //println("just before checkout deliveryType $deliveryType")

                currentTime.split("(").firstOrNull()?.let { it1 ->
                    viewModelCart.checkout(userType, listCheckoutProducts, costdelivered,
                        it1, deliveryType)
                }
                val refOrder = ""

                viewModelCart.refOrder.observe(requireActivity()) {
                    if (viewModelCart.refOrder.value != null) {


                        val paymentMethod = PaymenthMethodsAdyen(
                            amount =
                            Amount(currency = "EUR", value = getTotalPriceAdyen()),
                            merchantAccount = "MiscotaCOM",
                            reference = viewModelCart.refOrder.value!!
                        )


                        val paymentRequest = PaymentRequestAdyen(
                            amount =
                            Amount(currency = "EUR", value = getTotalPriceAdyen()),
                            merchantAccount = BuildConfig.MERCHANT_ACCOUNT_TEST_MIS,
                            paymentMethod = payment!!,
                            reference = viewModelCart.refOrder.value!!,
                            returnUrl = "miscota://paymentResult"
                        )
                        viewModelCart.fetchPaymentTest(paymentRequest)
                        //viewModelCart.fetchPaymentPro(paymentRequest)

                        viewModelCart.checkoutResult.observe(requireActivity()) {

                            var pedido = viewModelCart.checkoutResult.value
                            val resultOk = viewModelCart.refOrder.value

                            val bundleEcommerce = Bundle()
                            bundleEcommerce.putString(getString(R.string.get_order_ref_number), resultOk)
                            if (paymentResult) {

                                val fragment = Pedido()
                                fragment.arguments = bundleEcommerce
                                replaceFragment(fragment)

                                val screenName = "checkout_finished_ok"
                                val eventsInfo: EventsInfo
                                        = EventsInfo( screenName = screenName,
                                    methodName = "fetchPaymentPro",
                                    screenClass = "TramitarPedidoFragment")

                                //cartItem: CartItem, order: String, item: Bundle, cartShip: Double, total: Double, eventsInfo: EventsInfo
                                //viewModel.eventsManager.purchase(arrayOf(list),)

                                /**firebaseAnalytics.logEvent(FirebaseAnalytics.Event.PURCHASE) {
                                    param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
                                    param(FirebaseAnalytics.Param.SCREEN_CLASS, "TramitarPedidoFragment")
                                    param(FirebaseAnalytics.Param.TRANSACTION_ID, viewModelCart.refOrder.value!!)
                                    param(FirebaseAnalytics.Param.AFFILIATION, "Miscota App Android 2.0")
                                    param(FirebaseAnalytics.Param.VALUE,getTotalPriceAnalytics())
                                    param(FirebaseAnalytics.Param.CURRENCY, "EUR")
                                    param(FirebaseAnalytics.Param.METHOD, "fetchPaymentTest")
                                }**/

                                //vaciar carrito
                                /**listCheckoutProducts.map {
                                    viewModelCart.removeItemRef(it.ref, it.type?:getString(R.string.type_ecommerce), requireContext())
                                    viewModelCart.removeItemRef(it.ref, it.type?:getString(R.string.type_sameday), requireContext())
                                }**/


                            } else if (!paymentResult) {
                                /**val fm: FragmentManager = requireActivity().supportFragmentManager
                                val ft: FragmentTransaction = fm.beginTransaction()

                                var fragment = PedidoNoProcesado()
                                ft.add(R.id.thankyou, PedidoNoProcesado())
                                //fragment.arguments = bundleEcommerce
                                ft.commit()**/
                                Toast.makeText(requireContext(), getString(R.string.checkout_failed_message_new),
                                    Toast.LENGTH_LONG).show()
                            }

                        }

                    }
                    return@observe
                }


            }else if( !checkoutInit ){
                /**val fm: FragmentManager = requireActivity().supportFragmentManager
                val ft: FragmentTransaction = fm.beginTransaction()

                var fragment = PedidoNoProcesado()
                ft.add(R.id.thankyou, PedidoNoProcesado())
                //fragment.arguments = bundleEcommerce
                ft.commit()**/
                /**Toast.makeText(requireContext(),
                    getString(R.string.checkout_failed_message),
                    Toast.LENGTH_LONG).show()**/
                viewModelCart.showLoading.value = false

            }
        }

        binding.addressOrderCard.setOnClickListener{

            val fragment: Fragment = AddressCurrentFragment()
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.thankyou, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        binding.paymentMethodCard.setOnClickListener {

            val fragment: Fragment = PaymentMethodFragment()
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.thankyou, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        binding.goEditDiscount.setOnClickListener {

            val fragment: Fragment = DiscountFragment()
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.thankyou, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()

        }

        binding.typeOrderCard.setOnClickListener {
            val fragment: Fragment = TipoEnvioFragment()
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.thankyou, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()

        }

        val list = viewModelCart.items
        list.value?.map {
            //println(" it tramitar pedido $it ")
        }
       for (item in list.value!!)
           println(" item tramitar pedido$item")

        val listTwo = viewModelCart.authStore.getCart()
        listTwo.map {
            it.currentTimeDelivered
            //println(" it.currentTimeDelivered ${it.currentTimeDelivered}")
            //println(" it.deliveredTypeOne ${it.deliveredTypeOne}")
        }

        listAdapter =
            TramitarPedidoImageAdapter(
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

    private fun getTotalPriceAdyen(): Int{
        val price = binding.totalPrice.text.toString().split(" ").toTypedArray()
        var allPrice = price[0].replace(".","")
        allPrice = price[0].replace(",","")
        val priceToAdyen = allPrice.toInt()
        println(" proceeded ${price[0].replace(".","")}")
        return priceToAdyen
    }

    //total price to firebase analytics purchase
    private fun getTotalPriceAnalytics(): Double{
        val price = binding.totalPrice.text.toString().split(" ").toTypedArray()
        var allPrice = price[0].replace(".","")
        allPrice = price[0].replace(",","")
        val priceAnalytics = price[0].replace(",",".")

        return priceAnalytics.toDouble()
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
        val expiryDate = ExpiryDate(expireDate.toInt(), expire.toInt())
        val securityCode = securityCode

        try {

        val cardType =
            CardType.estimate(cardNumber)[0] // This is just an estimation and could be empty

        if (CardValidationUtils.validateCardNumber(cardNumber).isValid
            && CardValidationUtils.validateExpiryDate(expiryDate).isValid
            && CardValidationUtils.validateSecurityCode(securityCode, cardType).isValid
        ) {
            val rawCardData = Card.Builder()
                .setNumber(cardNumber)
                .setExpiryDate(expiryDate.expiryMonth, expiryDate.expiryYear)
                .setSecurityCode(securityCode)
                .build()

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
            paymentResult = true

        }else{
            Toast.makeText(requireContext(),"Tarjeta inválida",Toast.LENGTH_SHORT).show()
            return PaymentMethod(
                encryptedCardNumber = "0000 0000 0000 0000",
                encryptedExpiryMonth =  "00/00",
                encryptedExpiryYear =  "0/00",
                encryptedSecurityCode =  "000",
                encryptedUserName = "user name default")
            paymentResult = false

        }
        }catch (e: IndexOutOfBoundsException){
            println("Exception Card ${e.message}  ${e.printStackTrace()}  $e")
            viewModelCart.showLoading.value = false

            Toast.makeText(requireContext(),"Tarjeta inválida, compruebe la tarjeta",Toast.LENGTH_SHORT).show()
            return PaymentMethod(
                encryptedCardNumber = "0000 0000 0000 0000",
                encryptedExpiryMonth =  "00/00",
                encryptedExpiryYear =  "00/00",
                encryptedSecurityCode =  "000",
                encryptedUserName = "user name default")

            val card = CardN(card="0000 0000 0000 0000", security= "000",expireYear="00/00".split("/").get(1), expireMonth="00/00".split("/").first(), owner="user name default")
            viewModelCart.authStore.setCard(card)

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
        email: String,
        cardNumber: String,
        deliveryType: String,
        currentTime: String
    ): Boolean{

        var checkoutStock = true
        var checkoutItems = true
        var checkoutUser = true
        var checkoutAddress = true
        var checkoutCard = false
        var checkoutDelivery = false
        //var payment = true

        println("deliveryType $deliveryType")
        println("currentTime $currentTime")

        if(list.size == 0){

            Toast.makeText(requireContext(), getString(R.string.message_need_product_in_cart),
                Toast.LENGTH_LONG).show()
            checkoutItems = false
        }
        if ((addressThird.postalCode == null && addressThird.address == null &&
                    addressThird.city == null && addressThird.region == null ))
        {
            Toast.makeText(requireContext(), getString(R.string.message_need_address_in_cart),
                Toast.LENGTH_LONG).show()

            startActivity(Intent(requireContext(), AddressActivity::class.java))
            checkoutAddress = false
        }
        if(!isLoggedIn() && viewModelCart.authStore.getUser() == null){

            Toast.makeText(requireContext(), getString(R.string.login_off_message),
                Toast.LENGTH_LONG).show()
            viewModelCart.showLoading.value = false
            startActivity(Intent(requireActivity(), AuthActivity::class.java))
            checkoutUser = false
        }
        if (currentTime.length > 1){

            checkoutDelivery = true
            viewModelCart.showLoading.value = true

        }
        if (cardNumber != null){

            if (cardNumber.length == MAX_LENGTH_CARD){
                checkoutCard = true
            }else{

                Toast.makeText(requireContext(), getString(R.string.payment_off_message),
                    Toast.LENGTH_LONG).show()
                viewModelCart.showLoading.value = false
                checkoutCard = false

            }
        }
        if(currentTime.length == 1){
            viewModelCart.showLoading.value = false
            checkoutDelivery = false

            Toast.makeText(requireContext(), getString(R.string.delivery_type_message),
                Toast.LENGTH_LONG).show()
        }
        if (deliveryType.isEmpty() && currentTime.isEmpty() && viewModelCart.authStore.getType() == getString(R.string.type_sameday)){
            viewModelCart.showLoading.value = false
            checkoutDelivery = false

            Toast.makeText(requireContext(), getString(R.string.delivery_type_message),
                Toast.LENGTH_LONG).show()

        }
        if (viewModelCart.authStore.getType() == getString(R.string.type_sameday) && deliveryType == getString(R.string.delivery_type_default) && currentTime == getString(R.string.current_time_default)){

            viewModelCart.showLoading.value = false
            checkoutDelivery = false

            Toast.makeText(requireContext(), getString(R.string.delivery_type_message),
                Toast.LENGTH_LONG).show()

        }
        if (viewModelCart.authStore.getType()  == getString(R.string.type_ecommerce)){
            viewModelCart.showLoading.value = true
            checkoutDelivery = true
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
        return !(!checkoutAddress || !checkoutItems || !checkoutStock || !checkoutUser || !checkoutCard || !checkoutDelivery )
    }

    fun isLoggedIn(): Boolean {
        return viewModelCart.authStore.isLoggedIn()
    }

    private fun loadCheckout(): MutableList<CartUiModel.ItemListCheckout>{

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

            listMoreInfo.add(
                CartUiModel.MoreInfoCheckout(
                    guess = if (viewModelCart.authStore.getUser() != null) false else true,
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

    private fun goToCartScreen() {
        startActivity(Intent(requireActivity(), CartActivity::class.java))
    }

    private fun loadCart(): List<CartItem>{
        val list: MutableList<CartItem> = mutableListOf()
        val cartItems = viewModelCart.authStore.getCart().map {
            //it.toCartUiModel()
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

        //val currentTimeDelivered = "22/09/2021"
        val cartItemsTest = viewModelCart.authStore.getCart().toMutableList()
        val newList = cartItemsTest.map { item ->
            //return@map item.copy(currentTimeDelivered = currentTimeDelivered)
            return@map item
        }

        return list
    }

    fun loadCartToItem() {
        val list: MutableList<CartUiModel> = mutableListOf()
        var totalSameDay = 0
        var totalEcommerce = 0
        var currentDelivered: String? = null
        val cartItems = viewModelCart.authStore.getCart().map {

            it.toCartItemUiModel()

            if (it.toCartUiModel().type == getString(R.string.type_sameday)){
                totalSameDay += it.toCartUiModel().quantity

                if (it.toCartUiModel().samedayDelivery == null){
                    binding.dateOrderReceiveSameDay.text = getString(R.string.text_need_hour_receive_sd)

                }
                if ( it.toCartUiModel().samedayDelivery?.length == 0 && (it.toCartUiModel().samedayDelivery == "" || it.toCartUiModel().samedayDelivery == CURRENT_ZERO_DEFAULT )){
                    binding.dateOrderReceiveSameDay.text = getString(R.string.text_need_hour_receive_sd)

                }
                if (it.toCartUiModel().samedayDelivery != null && it.toCartUiModel().samedayDelivery != "" && it.toCartUiModel().samedayDelivery != CURRENT_ZERO_DEFAULT ){
                    currentDelivered = it.toCartUiModel().samedayDelivery
                    binding.dateOrderReceiveSameDay.text = it.toCartUiModel().samedayDelivery
                }

            }
            if (it.toCartUiModel().type == getString(R.string.type_ecommerce)){
                totalEcommerce += it.toCartUiModel().quantity
            }


        }

        val bundleThankYou = Bundle()
        bundleThankYou.putString(
            getString(R.string.total_ecommerce),
            totalEcommerce.toString()
        )
        bundleThankYou.putString(
            getString(R.string.total_sameday),
            totalSameDay.toString()
        )

        if ( totalSameDay > 0 ){

            binding.totalProductsCartSameDay.text = String.format(resources.getString(R.string.total_products_exemple),totalSameDay)

            binding.totalProductsCartSameDay.visibility = View.VISIBLE
            binding.typeOrderSameday.visibility = View.VISIBLE
            binding.imageSameday.visibility = View.VISIBLE

        }
        if ( totalEcommerce > 0 ){

            binding.totalProductsCartEcommerce.text = String.format(resources.getString(R.string.total_products_exemple),totalEcommerce.toString())

        }
        if ( totalSameDay == 0 ){

            binding.totalProductsCartSameDay.text = String.format(resources.getString(R.string.total_products_exemple), CURRENT_ZERO_DEFAULT )
            binding.dateOrderReceiveSameDay.visibility = View.GONE
            binding.infoOrderReceiveSameday.visibility = View.GONE
            binding.totalProductsCartSameDay.visibility = View.GONE
            binding.typeOrderSameday.visibility = View.GONE
            binding.imageSameday.visibility = View.GONE

            val params = (binding.typeOrderEcommerce.layoutParams as ViewGroup.MarginLayoutParams)
            params.setMargins(0, 90, 0, 20)
            binding.typeOrderEcommerce.layoutParams = params

            val paramsTwo = (binding.totalProductsCartEcommerce.layoutParams as ViewGroup.MarginLayoutParams)
            paramsTwo.setMargins(0, 90, 0, 20)
            binding.totalProductsCartEcommerce.layoutParams = paramsTwo

        }
        if ( totalEcommerce == 0 ){
            binding.totalProductsCartEcommerce.text = String.format(resources.getString(R.string.total_products_exemple),CURRENT_ZERO_DEFAULT)
            binding.typeOrderEcommerce.visibility = View.GONE
            binding.totalProductsCartEcommerce.visibility = View.GONE
        }

    }

    private fun isLogued(): Boolean {
        return viewModelCart.authStore.isLoggedIn()
    }

    fun loadRecentAddresses() {
        recentAddresses = viewModelCart.authStore.getRecentAddresses() ?: listOf()
    }

    private fun loadAddressesUser() {
        recentAddressesUser = viewModelCart.authStore.getRecentAddressesInfo() ?: listOf()
    }

    private fun loadAddressUser(){
        addressUser = viewModelCart.authStore.getAddress()
    }

    private fun loadAddressInfo(){
        addressUserInfo = viewModelCart.authStore.getAddressInfo()
    }

    override fun onStart() {
        super.onStart()
        println("onStart TramitarPedido")

        val cartItemsTest = viewModelCart.authStore.getCart().toMutableList()
        cartItemsTest.map { item ->
            if(item.toCartUiModel().type == getString(R.string.type_sameday)) {

                if (item.toCartUiModel().samedayDelivery != null && item.toCartUiModel().samedayDelivery == "") {
                    binding.dateOrderReceiveSameDay.text =
                        getString(R.string.text_need_hour_receive_sd)

                }
                if(item.toCartUiModel().samedayDelivery == CURRENT_ZERO_DEFAULT ){

                    binding.dateOrderReceiveSameDay.text =
                        getString(R.string.text_need_hour_receive_sd)

                }
                if(item.toCartUiModel().samedayDelivery == null){

                    binding.dateOrderReceiveSameDay.text =
                        getString(R.string.text_need_hour_receive_sd)

                }
                if (item.toCartUiModel().samedayDelivery != null && item.toCartUiModel().samedayDelivery?.length == 0 && item.toCartUiModel().samedayDelivery != "") {

                    binding.dateOrderReceiveSameDay.text =
                        getString(R.string.text_need_hour_receive_sd)

                }
                if (item.toCartUiModel().samedayDelivery != null &&  item.toCartUiModel().samedayDelivery != "" && item.toCartUiModel().samedayDelivery != CURRENT_ZERO_DEFAULT ) {

                    binding.dateOrderReceiveSameDay.text = item.toCartUiModel().samedayDelivery
                }

            }

            viewModelCart.costSd.observe(requireActivity()){
                return@observe
            }
            viewModelCart.costEcommerce.observe(requireActivity()){
                return@observe
            }

            return@map item
        }


    }

    /**override fun onResume() {
        super.onResume()
        println("onResume TramitarPedido")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        println("onAttach TramitarPedido")

    }

    override fun onPause() {
        super.onPause()
        println("onPause TramitarPedido")
    }**/

    override fun onStop() {
        super.onStop()
        println("onStop TramitarPedido")
        val cartItemsTest = viewModelCart.authStore.getCart().toMutableList()
        val newList = cartItemsTest.map { item ->
            //viewModelCart.authStore.setCurrentTimeDelivered("0")
            return@map item
        }
    }

   /** override fun onDestroyView() {
        super.onDestroyView()
        println("onDestroyView")
    }

    override fun onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu()
        println("onDestroyOptionsMenu")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        println("onDetach")
    }**/

}