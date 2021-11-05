package com.miscota.android.ui.tramitarpedido

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.Toast
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
        const val maxLengthCard = 16
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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TramitarPedidoFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        loadRecentAddresses()
        loadAddressesUser()
        loadAddressUser()
        loadAddressInfo()

        loadCartToItem()


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


        //val currentTime: String? = currentTimeBundle?.getString(getString(R.string.date_shipping_sameday))

        if( cardNumber != null && cardMonth != null && cardYear !=  null && cardSecurity != null && cardNumber.isNotEmpty()) {
            paymentMethod = encrypt(cardNumber, cardMonth.split("/").first(), cardYear.split("/").get(1), cardSecurity, cardOwner?:"OwnerNameDefault")

            val card = CardN(card=cardNumber, security= cardSecurity,expireYear=cardYear.split("/").get(1), expireMonth=cardMonth.split("/").first(), owner=cardOwner)
            viewModelCart.authStore.setCard(card)

        }

        val subtotal: Double = viewModelCart.authStore.getCart().map {
            it.combinationPrice * it.qty
        }.foldRight(0.0) { acc, d -> acc + d }

        val totalCheckout = subtotal + (viewModelCart.authStore.getCarriersSd()?.toDouble() ?: 0.0) + (viewModelCart.authStore.getCarriersEco()
            ?.toDouble() ?:0.0)
        binding.productsPrice.text = String.format("%.2f", subtotal)+" €"
        binding.sameDayPrice.text = viewModelCart.authStore.getCarriersSd()+" €"
        binding.ecommercePrice.text = viewModelCart.authStore.getCarriersEco()+" €"
        binding.totalPrice.text = String.format("%.2f", totalCheckout)+" €"

        if (viewModelCart.authStore.getCard() != null){

            binding.creditCardNumber.text = String.format(resources.getString(R.string.card_mask), viewModelCart.authStore.getCard()!!.card.substring(12,maxLengthCard))
            binding.creditCardName.text = viewModelCart.authStore.getCard()!!.owner.toString()
        }
        if( cardNumber != null ){

            binding.creditCardNumber.text = String.format(resources.getString(R.string.card_mask),cardNumber.substring(12,maxLengthCard))

        }
        if(cardOwner != null ){
            binding.creditCardName.text = cardOwner.toString()

        }

        if (addressB != null && provinceB != null && postalCodeB != null && cityB != null && phoneB != null){

            binding.addressShipping.text = addressB
            binding.addressShippingComplement.text = "$postalCodeB, $cityB, $phoneB ,$provinceB, España"
            viewModelCart.authStore.setPhone(phoneB)
        }
        if (addressBC != null && provinceBC != null && postalCodeBC != null && cityBC != null && phoneBC != null){

            binding.addressShipping.text = addressBC
            binding.addressShippingComplement.text = "$postalCodeBC, $cityBC, $phoneBC ,$provinceBC, España"
            viewModelCart.authStore.setPhone(phoneBC)

        }
        if (addressUser?.addressNumber != null){
            binding.addressShipping.text = addressUser?.addressNumber
            binding.addressShippingComplement.text = "${addressUser?.postalCode}, ${addressUser?.city}, ${addressUser?.state} ,${addressUser?.region}, España"

        }
        else {
            if (addressUserInfo != null){

                binding.addressShipping.text = addressUserInfo?.addressNumber
                binding.addressShippingComplement.text = "${addressUserInfo?.postalCode}, ${addressUserInfo?.city}, ${addressUserInfo?.state} ,${addressUserInfo?.region}, España"

            }else {

                if (recentAddressesUser.isNotEmpty()) {

                    binding.addressShipping.text = recentAddressesUser.first().addressNumber
                    binding.addressShippingComplement.text =
                        "${recentAddressesUser.first().postalCode}, ${recentAddressesUser.first().city}, ${recentAddressesUser.first().state}, ${recentAddressesUser.first().countryName}"

                } else {
                    if (recentAddresses.isNotEmpty()) {

                        recentAddresses.map {
                            it.address
                            binding.addressShipping.text = it.addressNumber
                            binding.addressShippingComplement.text =
                                "${it.postalCode}, ${it.city}, ${it.state}, ${it.countryName}"
                        }
                    }
                }
            }
        }

        if(isLogued()){
            binding.clientName.text = "${viewModelCart.authStore.getUser()?.name}"

            if (viewModelCart.authStore.getUser()?.surname != null){
                binding.clientName.text = "${viewModelCart.authStore.getUser()?.name}, ${viewModelCart.authStore.getUser()?.surname}"
            }
        }else{
            binding.clientName.text = getString(R.string.need_login)
        }

        val bundleCategory: Bundle? = arguments
        val pedido: String? = bundleCategory?.getString("resultOk")

        binding.toolbar.imageBack.setOnClickListener {
            val cartItemsTest = viewModelCart.authStore.getCart().toMutableList()
            val newList = cartItemsTest.map { item ->
                    viewModelCart.authStore.setCurrentTimeDelivered("0")
                //return@map item.copy(currentTimeDelivered = currentTimeDelivered)
                return@map item
            }

            goToCartScreen()
            requireActivity().finish()

        }

        binding.productTotalCart.text = "( ${viewModelCart.getTotalItens()} )"



        binding.buyNowButtonCard.setOnClickListener {

            val cartItems: LiveData<List<CartUiModel>> = viewModelCart.items
            cartItems.observe(requireActivity()) {
                println("\n viewModelTramitar.items $it \n")
            }

            listAdapter.hasObservers()

            var deliveryType = "DEFAULT"
            var currentTime = "DEFAULT_TIME"

            viewModelCart.authStore.getCart().map {


                deliveryType = it.deliveredTypeOne
                currentTime = it.currentTimeDelivered.toString()

                if (it.product.stockItens == 0) {
                    Toast.makeText(
                        requireContext(),
                        "De este producto tramitar: \n ${it.product.title} ${it.productId}no tenemos stock \n Lo sentimos mucho",
                        Toast.LENGTH_SHORT
                    ).show()

                }
                if (it.stock == 0) {
                    Toast.makeText(
                        requireContext(),
                        "De este producto tramitar: \n ${it.product.title} ${it.productId}no tenemos stock \n Lo sentimos mucho",
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
                cardNumber?:viewModelCart.authStore.getCard()?.card?:"0"
            )




            if (checkoutInit){
                //firebase analytics begin checkout
                listCheckoutProducts.map {
                    //val item = viewModelCart.eventsManager.itemCheckout(it)
                    //viewModelCart.eventsManager.beginCheckout(item, getTotalPriceAnalytics())
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

                println("just before checkout currentTime ${currentTime.split("(").firstOrNull()}")

                println("just before checkout deliveryType $deliveryType")

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
                        //viewModelCart.fetchPaymentTest(paymentRequest)
                        viewModelCart.fetchPaymentPro(paymentRequest)

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
                                    screenClass = "CartActivity")

                                //cartItem: CartItem, order: String, item: Bundle, cartShip: Double, total: Double, eventsInfo: EventsInfo
                                //viewModel.eventsManager.purchase(arrayOf(list),)

                                /**firebaseAnalytics.logEvent(FirebaseAnalytics.Event.PURCHASE) {
                                    param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
                                    param(FirebaseAnalytics.Param.SCREEN_CLASS, "CartActivity")
                                    param(FirebaseAnalytics.Param.TRANSACTION_ID, viewModel.refOrder.value!!)
                                    param(FirebaseAnalytics.Param.AFFILIATION, "Miscota App Android")
                                    param(FirebaseAnalytics.Param.VALUE,getTotalPriceAnalytics())
                                    param(FirebaseAnalytics.Param.CURRENCY, "EUR")
                                    param(FirebaseAnalytics.Param.METHOD, "fetchPaymentPro")
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
                                Toast.makeText(requireContext(),
                                    "No se ha podido realizar el pedido, por favor compruebe que no falten datos por completar ",
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
                Toast.makeText(requireContext(),
                    "No se ha podido realizar el pedido, por favor compruebe que no falten datos por completar ",
                    Toast.LENGTH_LONG).show()

            }
            /**listAdapter.currentList.size
            listAdapter.currentList.map {
                println("\n listAdapter.currentList.map it $it")
            }**/


            /**println("\n Subtotal y uid unico para cada venta ${listAdapter.currentList.get(0)}")
            println("\n SummaryItem = direction, time ${listAdapter.currentList.get(listAdapter.currentList.size-1)}")

            for (item in listAdapter.currentList)
                println(" \n for item listable $item")
            println(" \n for item cartItems.value ${cartItems.value?.get(0)}")**/
            
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
            println(" it tramitar pedido $it ")
        }
       for (item in list.value!!)
           println(" item tramitar pedido$item")

        val listTwo = viewModelCart.authStore.getCart()
        listTwo.map {
            it.currentTimeDelivered
            println(" it.currentTimeDelivered ${it.currentTimeDelivered}")
            println(" it.deliveredTypeOne ${it.deliveredTypeOne}")
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
            //println(" rawCardData $rawCardData")

            val encryptedCard = Encryptor.INSTANCE.encryptFields(
                rawCardData,
                //PUBLIC_KEY
                BuildConfig.PUBLIC_KEY_MIS
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
        cardNumber: String
    ): Boolean{

        var checkoutStock = true
        var checkoutItems = true
        var checkoutUser = true
        var checkoutAddress = true
        var checkoutCard = false
        //var payment = true

        if(list.size == 0){

            Toast.makeText(requireContext(), getString(R.string.message_need_product_in_cart),
                Toast.LENGTH_LONG).show()

            checkoutItems = false
        }
        else if ((addressThird.postalCode == null && addressThird.address == null &&
                    addressThird.city == null && addressThird.region == null ))
        {
            Toast.makeText(requireContext(), getString(R.string.message_need_address_in_cart),
                Toast.LENGTH_LONG).show()

            startActivity(Intent(requireContext(), AddressActivity::class.java))
            checkoutAddress = false
        }
        else if(!isLoggedIn() && viewModelCart.authStore.getUser() == null){

            Toast.makeText(requireContext(), "Faltan datos de usuario para completar tu compra",
                Toast.LENGTH_LONG).show()
            checkoutUser = false
        }
        else if (cardNumber != null){

            if (cardNumber.length == maxLengthCard){
                checkoutCard = true
            }else{

                Toast.makeText(requireContext(), "Faltan datos de pago para completar tu compra",
                    Toast.LENGTH_LONG).show()
                checkoutCard = false

            }
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
        return !(!checkoutAddress || !checkoutItems || !checkoutStock || !checkoutUser || !checkoutCard )
    }

    fun isLoggedIn(): Boolean {
        return viewModelCart.authStore.isLoggedIn()
    }

    private fun loadCheckout(): MutableList<CartUiModel.ItemListCheckout>{

        val list: MutableList<CartUiModel.ItemListCheckout> = mutableListOf()
        viewModelCart.authStore.getCart().map {
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
            //val itemCart = viewModel.eventsManager.viewItemCart(it)
            //viewModel.eventsManager.viewCart(it, itemCart)
            //endfirebase analytics

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
            if ((it.toCartUiModel().samedayDelivery == "" || it.toCartUiModel().samedayDelivery == "0") && it.toCartUiModel().type == getString(R.string.type_sameday)){
                binding.dateOrderReceiveSameDay.text = getString(R.string.text_need_hour_receive_sd)
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
            binding.infoOrderReceiveSameday.visibility = View.GONE
        }
        if ( totalEcommerce == 0 ){
            binding.totalProductsCartEcommerce.text = String.format(resources.getString(R.string.total_products_exemple),"0")
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
        println("onStart")

        val cartItemsTest = viewModelCart.authStore.getCart().toMutableList()
        cartItemsTest.map { item ->
            if ((item.toCartUiModel().samedayDelivery == "" || item.toCartUiModel().samedayDelivery == "0")
                && item.toCartUiModel().type == getString(R.string.type_sameday)){
                binding.dateOrderReceiveSameDay.text = getString(R.string.text_need_hour_receive_sd)
            }

            println("item.currentTimeDelivered, item.deliveredTypeOne} ${item.currentTimeDelivered} , ${item.deliveredTypeOne}")
            return@map item
        }


    }

    override fun onResume() {
        super.onResume()
        println("onResume")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        println("onAttach")
    }

    override fun onPause() {
        super.onPause()
        println("onPause")
    }

    override fun onStop() {
        super.onStop()
        println("onStop")
        val cartItemsTest = viewModelCart.authStore.getCart().toMutableList()
        val newList = cartItemsTest.map { item ->
            viewModelCart.authStore.setCurrentTimeDelivered("0")
            return@map item
        }
    }

    override fun onDestroyView() {
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
    }
}