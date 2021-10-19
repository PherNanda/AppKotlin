package com.miscota.android.ui.cart


import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.adyen.checkout.card.CardValidationUtils
import com.adyen.checkout.card.data.CardType
import com.adyen.checkout.card.data.ExpiryDate
import com.adyen.checkout.cse.Card
import com.adyen.checkout.cse.Encryptor
import com.miscota.android.BuildConfig
import com.miscota.android.R
import com.miscota.android.databinding.*
import com.miscota.android.ui.checkoutpayment.PaymentMethod
import com.miscota.android.util.AuthStore
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CartItemAdapter(
    val deleteItemClickListener: (CartUiModel.Item) -> Unit,
    private val addItemClickListener: (CartUiModel.Item) -> Unit,
    private val removeItemClickListener: (CartUiModel.Item) -> Unit,
    private val deliveredTypeClickListener: (CartUiModel.SummaryItem) -> Unit,
    private val changeAddress: () -> Unit,
    private val userLogued: String?,
    private val isLogued: Boolean,
    private val userZone: () -> Unit,
    private val currentTimeDelivered: (CartUiModel.SummaryItem) -> Unit,
    private val type: (CartUiModel.Item) -> Unit,
    private val authStore: AuthStore,
    private val carriers: (CartUiModel.SummaryItem) -> Unit,
    private val carriersSd: (CartUiModel.SummaryItem) -> Unit,
    private val carriersItem: () -> Unit,
    private val carriersSdItem: (CartUiModel.Item) -> Unit,
    private var types: ArrayList<String>?,
    private val payment: (CartUiModel.SummaryItem) -> Unit,
    private val deliveredNextDay: (CartUiModel.SummaryItem) -> Unit
): ListAdapter<CartUiModel, CartItemAdapter.ListViewHolder>(ListDiffUtil)  {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return when (ItemType.values()[viewType]) {
            ItemType.Loader -> {
                ListViewHolder.LoaderViewHolder(
                    ItemLoaderBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            ItemType.Item -> {
                ListViewHolder.ItemViewHolder(
                    ItemCartBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    deleteItemClickListener = deleteItemClickListener,
                    addItemClickListener = addItemClickListener,
                    removeItemClickListener = removeItemClickListener,
                    carriersItem = carriersItem,
                    carriersSdItem = carriersSdItem,
                    type = type,
                    types = types,
                    authStore = authStore,
                    bindingItemCart = ItemCartSummaryBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            ItemType.Spacer -> {
                ListViewHolder.SpacerViewHolder(
                    ItemSpacerBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            ItemType.Summary -> {
                ListViewHolder.ItemSummaryViewHolder(
                    ItemCartSummaryBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    changeAddress,
                    userLogued ?: "invitado",
                    isLogued,
                    userZone,
                    deliveredTypeClickListener,
                    currentTimeDelivered,
                    authStore,
                    carriers,
                    carriersSd,
                    type, types,
                    payment,
                    deliveredNextDay
                )
            }

            ItemType.Subtotal -> {
                ListViewHolder.ItemSubtotalViewHolder(
                    ItemCartSubtotalBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

        }
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            CartUiModel.LoaderItem -> {
                ItemType.Loader.ordinal
            }
            is CartUiModel.Item -> {
                ItemType.Item.ordinal
            }
            CartUiModel.SpacerItem -> {
                ItemType.Spacer.ordinal
            }
            is CartUiModel.SummaryItem -> {
                ItemType.Summary.ordinal
            }
            is CartUiModel.Subtotal -> {
                ItemType.Subtotal.ordinal
            }

        }
    }

    enum class ItemType {
        Loader,
        Item,
        Spacer,
        Summary,
        Subtotal
    }

    sealed class ListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        abstract fun bind(item: CartUiModel)

        class LoaderViewHolder(binding: ItemLoaderBinding) : ListViewHolder(binding.root) {
            override fun bind(item: CartUiModel) {
            }
        }

        class SpacerViewHolder(binding: ItemSpacerBinding) : ListViewHolder(binding.root) {
            override fun bind(item: CartUiModel) {
            }
        }

        class ItemViewHolder(
            private val binding: ItemCartBinding,
            private val bindingItemCart: ItemCartSummaryBinding,
            private val addItemClickListener: (CartUiModel.Item) -> Unit,
            private val removeItemClickListener: (CartUiModel.Item) -> Unit,
            private val deleteItemClickListener: (CartUiModel.Item) -> Unit,
            private val carriersItem: () -> Unit,
            private val carriersSdItem: (CartUiModel.Item) -> Unit,
            private val type: (CartUiModel.Item) -> Unit,
            private var types: ArrayList<String>?,
            private val authStore: AuthStore
        ) : ListViewHolder(binding.root) {

            override fun bind(item: CartUiModel) {
                val uiModel = item as CartUiModel.Item
                binding.productName.text = uiModel.productName
                binding.priceText.text = String.format("%.2f", uiModel.price)+" €"


                binding.quantityText.text = uiModel.quantity.toString()
                binding.productTypeCart.text =
                if (uiModel.type == "sameday") "entrega hoy" else "entrega estándar"

                if (uiModel.type == "sameday"){
                    println(" type product ${uiModel.type}")
                    binding.samedayIn.visibility = View.VISIBLE
                }

                println("type:::: ${uiModel.type} uimodel")
                types?.add(uiModel.type ?: "")

                binding.productImage.load(uiModel.image) {
                    error(R.color.placeholder)
                    placeholder(R.color.placeholder)
                }

                binding.brandProductName.text = uiModel.brand

                binding.addButton.setOnClickListener {
                    val quantity = binding.quantityText.text.toString().toInt() + 1
                    binding.quantityText.text = quantity.toString()
                    addItemClickListener.invoke(uiModel.copy(quantity = quantity))
                    carriersItem.invoke()
                    binding.removeButton.isEnabled = true
                    //println(" quantity.. $quantity")
                }

                binding.removeButton.setOnClickListener {
                    val quantity = binding.quantityText.text.toString().toInt() - 1
                    if (quantity > 0) {
                        binding.quantityText.text = quantity.toString()
                        removeItemClickListener.invoke(uiModel.copy(quantity = quantity))

                    }
                }

                //position

                binding.deleteButton.setOnClickListener {
                    deleteItemClickListener.invoke(uiModel)
                }

            }
        }

        /**fun removeAt(position: Int) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }**/

        class ItemSummaryViewHolder(
            private val binding: ItemCartSummaryBinding,
            private val changeAddress: () -> Unit,
            private val userLogued: String,
            private val isLogued: Boolean,
            private val userZone: () -> Unit,
            private val deliveredTypeClickListener: (CartUiModel.SummaryItem) -> Unit,
            private val currentTimeDelivered: (CartUiModel.SummaryItem) -> Unit,
            private val authStore: AuthStore,
            private val carriers: (CartUiModel.SummaryItem) -> Unit,
            private val carriersSd: (CartUiModel.SummaryItem) -> Unit,
            private val type: (CartUiModel.Item) -> Unit,
            private var types: ArrayList<String>?,
            private val payment: (CartUiModel.SummaryItem) -> Unit,
            private val deliveredNextDay: (CartUiModel.SummaryItem) -> Unit
        ) : ListViewHolder(binding.root) {


            lateinit var paymentMethod: PaymentMethod
            private var paymentMethodWithout: PaymentMethod? = null

            override fun bind(item: CartUiModel) {
                val uiModel = item as CartUiModel.SummaryItem


                println("item.typeOne.size ${item.typeOne?.size}")


                if (isLogued) {
                    binding.addressText.text =
                        authStore.getAddressInfo()?.address + ", " + authStore.getAddressInfo()?.state + "" +
                                ", " + authStore.getAddressInfo()?.postalCode + ", " + authStore.getAddressInfo()?.city
                    println("authStore.getAddress()  ${authStore.getAddress()}")

                    item.typeOne?.map {
                        it
                        println(" ittypeeeeeeee $it")
                    }

                    println("item.typeOne.size in ${item.typeOne?.size}")
                    if (binding.addressText.text == "null, null, null, null") {
                        binding.addressText.text =
                            "sin dirección añadida, por favor añada un dirección"
                    }

                } else {
                    //binding.addressText.text = "${uiModel.address?.address}, ${uiModel.address?.postalCode}, ${uiModel.address?.city}" ?: ""
                    binding.addressText.text = if(uiModel.address?.address != null) "${uiModel.address?.address}, ${uiModel.address.state}" else "Sin dirección añadida"
                    println("uiModel.address  ${uiModel.address}")
                    println("uiModel.address.address  ${uiModel.address?.address}")
                }


                binding.changeDirectionLabel.setOnClickListener {
                    changeAddress.invoke()
                    //binding.addressText.text = "sin dirección añadida, por favor añada un dirección"
                }

                binding.deliveryPriceText.visibility = View.GONE
                binding.deliveryPriceTextSd.visibility = View.GONE
                binding.delieveryDayTextEco.visibility = View.GONE
                binding.delieveryDayText.visibility = View.GONE
                binding.radioGroup.visibility = View.GONE


                types?.map {
                    if (types != null && it != "") {
                        if (it == "ecommerce") {
                            binding.deliveryPriceText.visibility = View.VISIBLE
                            binding.deliveryPriceText.text = "${authStore.getCarriersEco()} €"

                            binding.deliveryPriceText.doOnTextChanged { price, _, _, _ ->
                                carriers.invoke(
                                    uiModel.copy(
                                        costEco = authStore.getCarriersEco()?.toDouble() ?: 0.0
                                    )
                                )

                            }

                            binding.delieveryDayTextEco.visibility = View.VISIBLE
                            binding.radioGroup.visibility = View.GONE
                            binding.timeLabel.visibility = View.GONE

                            //println("  it ->> $it  ecommerce ")
                        }
                        if (it == "sameday") {
                            binding.delieveryDayText.visibility = View.VISIBLE
                            binding.radioGroup.visibility = View.VISIBLE
                            binding.timeLabel.visibility = View.VISIBLE
                            //carriersSd.invoke(uiModel.copy(costSd = authStore.getCarriersSd()?.toDouble()!!))
                            binding.deliveryPriceTextSd.doAfterTextChanged { uiModel.costSd }
                            binding.deliveryPriceTextSd.text = "${authStore.getCarriersSd()} €"
                            binding.deliveryPriceTextSd.visibility = View.VISIBLE

                            //println(" it ->> $it  sameday ")
                        }
                        //println(" typssssssssss $types it::::: $it")
                    } else {
                        //println(" typssssss ${types?.size}")
                    }
                }

                if (isLogued) {
                    binding.buyingGuestLabel.text = userLogued
                    binding.buyGuestEmail.isVisible = false
                    binding.buyGuestPhone.isVisible = false
                    binding.buyGuestPhone.height = 0
                    binding.buyGuestEmail.height = 0
                    binding.goToAccountLabel.visibility = View.GONE
                } else {
                    binding.buyingGuestLabel.text

                    binding.goToAccountLabel.setOnClickListener {
                        userZone.invoke()
                    }
                }


                val inputCardNumber = binding.cardNoEditText.text
                val inputDateCardNumber = binding.dateEditText.text
                val inputOwnerName = binding.ownerNameEditText.text
                val inputSecurity = binding.securityEditText.text

                binding.dateLayout.editText?.text = inputDateCardNumber
                binding.securityLayout.editText?.text = inputSecurity
                binding.ownerNameLayout.editText?.text = inputOwnerName
                binding.cardNoLayout.editText?.text = inputCardNumber

                var inputCard = inputCardNumber
                var inputDateCard = inputDateCardNumber
                var inputOwnerN = inputOwnerName
                var inputSecure = inputSecurity
                val bundleCheckout = Bundle()
                bundleCheckout.putString("inputCard", inputCard.toString())
                bundleCheckout.putString("inputDateCard", inputDateCard.toString())
                bundleCheckout.putString("inputOwner", inputOwnerN.toString())
                bundleCheckout.putString("inputSecure", inputSecure.toString())


                binding.cardNoEditText.doOnTextChanged { inputCardNumber, _, _, _ ->

                    if (inputCardNumber != null && inputCardNumber.length < 16) {
                        var cardNumberError = ""

                        if (TextUtils.isEmpty(binding.cardNoEditText.text) || inputCardNumber.length < 17) {
                            cardNumberError = "ponga todos los digitos de la tarjeta"
                        }
                        binding.cardNoLayout.error = cardNumberError
                        binding.cardNoLayout.isErrorEnabled = true

                    } else if (inputCardNumber != null && inputCardNumber.length == 16) {

                        var cardNumberMsgOk = ""
                        if (!TextUtils.isEmpty(binding.cardNoEditText.text) && inputCardNumber.length == 16) {
                            cardNumberMsgOk = "perfecto!"

                            inputCard = binding.cardNoEditText.text
                            println("\n inputCard $inputCard")
                            paymentMethodWithout =
                                PaymentMethod(
                                    type = "scheme",
                                    encryptedCardNumber = inputCard.toString(),
                                    encryptedExpiryMonth = inputDateCard.toString(),
                                    encryptedExpiryYear = inputDateCard.toString(),
                                    encryptedSecurityCode = inputSecure.toString(),
                                    encryptedUserName = inputOwnerN.toString()
                                )
                            payment.invoke(uiModel.copy(payment = paymentMethodWithout))

                        }

                        binding.cardNoLayout.error = cardNumberMsgOk
                        binding.cardNoLayout.isErrorEnabled = false

                        binding.cardNoEditText.text
                    }

                }

                binding.dateEditText.doOnTextChanged { inputDateCardNumber, _, _, _ ->

                    if (inputDateCardNumber != null && inputDateCardNumber.length < 5) {
                        var dateNumberError = ""

                        if (TextUtils.isEmpty(binding.dateEditText.text) || inputDateCardNumber.length < 5) {
                            dateNumberError = "ponga la caducidad de la tarjeta"
                        }
                        binding.dateEditText.error = dateNumberError
                        binding.dateLayout.isErrorEnabled = true

                    } else if (inputDateCardNumber != null && inputDateCardNumber.length == 5) {
                        binding.dateLayout.isErrorEnabled = false
                        binding.dateEditText.text

                        inputDateCard = binding.dateEditText.text
                        println("\n inputDateCard $inputDateCard")

                        paymentMethodWithout =
                            PaymentMethod(
                                type = "scheme",
                                encryptedCardNumber = inputCard.toString(),
                                encryptedExpiryMonth = inputDateCard.toString(),
                                encryptedExpiryYear = inputDateCard.toString(),
                                encryptedSecurityCode = inputSecure.toString(),
                                encryptedUserName = inputOwnerN.toString()
                            )
                        payment.invoke(uiModel.copy(payment = paymentMethodWithout))

                    }

                }

                binding.ownerNameEditText.doOnTextChanged { inputOwnerName, _, _, _ ->

                    if (inputOwnerName != null && inputOwnerName.length < 9) {
                        var ownerNameError = ""

                        if (TextUtils.isEmpty(binding.ownerNameEditText.text) || inputOwnerName.length < 9) {
                            ownerNameError = "ponga su nombre completo"
                        }
                        binding.ownerNameEditText.error = ownerNameError
                        binding.ownerNameLayout.isErrorEnabled = true

                    } else if (inputOwnerName != null && inputOwnerName.length > 7) {
                        binding.ownerNameLayout.isErrorEnabled = false
                        binding.ownerNameEditText.text

                        inputOwnerN = binding.ownerNameEditText.text
                        println("\n inputOwnerN $inputOwnerN")

                    }
                }

                binding.securityEditText.doOnTextChanged { inputSecurity, _, _, _ ->

                    if (inputSecurity != null && inputSecurity.length < 3) {
                        var securityNumberError = ""

                        if (TextUtils.isEmpty(binding.securityEditText.text) || inputSecurity.length < 3) {
                            securityNumberError = "ponga el CVV"
                        }
                        binding.securityEditText.error = securityNumberError
                        binding.securityLayout.isErrorEnabled = true

                    } else if (inputSecurity != null && inputSecurity.length == 3) {
                        binding.securityLayout.isErrorEnabled = false
                        binding.securityEditText.text

                        inputSecure = binding.securityEditText.text
                        println("\n inputSecure $inputSecure")

                        paymentMethodWithout =
                            PaymentMethod(
                                type = "scheme",
                                encryptedCardNumber = inputCard.toString(),
                                encryptedExpiryMonth = inputDateCard.toString(),
                                encryptedExpiryYear = inputDateCard.toString(),
                                encryptedSecurityCode = inputSecure.toString(),
                                encryptedUserName = inputOwnerN.toString()
                            )
                        payment.invoke(uiModel.copy(payment = paymentMethodWithout))

                    }
                }


                if (paymentMethodWithout?.encryptedSecurityCode != null) {

                    paymentMethod = encrypt(
                        paymentMethodWithout?.encryptedCardNumber!!,
                        paymentMethodWithout?.encryptedExpiryMonth!!.substring(0, 2),
                        paymentMethodWithout?.encryptedExpiryYear!!.substring(3, 5),
                        paymentMethodWithout?.encryptedSecurityCode!!,
                        inputOwnerN.toString()
                    )
                    payment.invoke(uiModel.copy(payment = paymentMethod))
                }

                val start = Calendar.getInstance()

                println(" start.time.hours ${start.time.hours} start.time.minutes ${start.time.minutes} ")

                if ((start.time.hours >= 19 && start.time.minutes > 0) ||
                    (start.time.hours >= 14 && start.time.minutes > 0)
                ) {
                    binding.button1nextDay.visibility = View.VISIBLE
                    binding.button2nextDay.visibility = View.VISIBLE
                    binding.button3nextDay.visibility = View.VISIBLE
                }
                if ((start.time.hours <= 18 && start.time.minutes >= 0)
                    || (start.time.hours <= 18 && start.time.minutes <= 59)
                ) {
                    binding.button3.visibility = View.VISIBLE
                }
                if ((start.time.hours <= 13 && start.time.minutes >= 0)
                    || (start.time.hours <= 13 && start.time.minutes <= 59)
                ) {
                    binding.button2.visibility = View.VISIBLE
                }
                if ((start.time.hours <= 9 && start.time.minutes >= 0)
                    || (start.time.hours <= 9 && start.time.minutes <= 59)
                ) {
                    binding.button1.visibility = View.VISIBLE
                }


                binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->

                    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss a")
                    val currentDate = sdf.format(Date())
                    val dateFormat: DateFormat = DateFormat.getDateInstance()
                    println(" currentDate $currentDate")


                    val medium = Calendar.getInstance()
                    val end = Calendar.getInstance()

                    val day: Int = 1
                    //start.add(Calendar.DATE, day)
                    start.time
                    println(" start.time.hours ${start.time.hours}")
                    start.time.minutes
                    println(" start.time.minutes ${start.time.minutes}")

                    val horaDia = Calendar.HOUR_OF_DAY
                    val hora = Calendar.HOUR

                    when (checkedId) {
                        R.id.button1 -> {

                            if (
                                (start.time.hours >= 10 && start.time.minutes > 0)
                            ) {
                                println("1 false")

                                println("start $start bt1")
                                binding.button1.isEnabled = false
                                binding.button1.isChecked = false
                                //binding.button1.text = "Horario de entrega no disponible"
                            } else {

                                binding.button1.isEnabled = true
                                binding.button1.text.toString()
                                println("1 true")
                                println("start $start bt1 else")
                                val startDay = Calendar.getInstance()
                                val day = 1

                                println("startDay $startDay bt1 else")

                                if (startDay.time.day == 7) {
                                    val deliveredTypeOne = binding.button1.text.toString()
                                    startDay.add(Calendar.DATE, day)
                                    deliveredTypeClickListener.invoke(uiModel.copy(deliveredTypeOne = "T1ND"))
                                    currentTimeDelivered.invoke(uiModel.copy(currentTimeDelivered = start.time.toString()))
                                } else {
                                    val deliveredTypeOne = binding.button1.text.toString()
                                    deliveredTypeClickListener.invoke(uiModel.copy(deliveredTypeOne = "T1SD"))
                                    currentTimeDelivered.invoke(uiModel.copy(currentTimeDelivered = start.time.toString()))
                                }

                            }
                        }
                        R.id.button2 -> {

                            if (
                                (start.time.hours >= 14 && start.time.minutes > 0)
                            ) {

                                binding.button2.isEnabled = false
                                binding.button2.isChecked = false

                            } else {
                                binding.button2.isEnabled = true
                                binding.button2.text.toString()

                                val startDays = Calendar.getInstance()
                                val day = 1

                                if (startDays.time.day == 7) {
                                    val deliveredTypeOne = binding.button2.text.toString()
                                    startDays.add(Calendar.DATE, day)
                                    deliveredTypeClickListener.invoke(uiModel.copy(deliveredTypeOne = "T2ND"))
                                    currentTimeDelivered.invoke(uiModel.copy(currentTimeDelivered = start.time.toString()))
                                } else {

                                    val deliveredTypeOne = binding.button2.text.toString()
                                    currentTimeDelivered.invoke(uiModel.copy(currentTimeDelivered = start.time.toString()))
                                    deliveredTypeClickListener.invoke(uiModel.copy(deliveredTypeOne = "T2SD"))
                                }


                            }
                        }
                        R.id.button3 -> {

                            if (start.time.hours >= 19 &&
                                start.time.minutes > 0
                            ) {

                                binding.button3.isEnabled = false

                                start.time
                                println("3 false")
                                println(" dia siguiente ${start.time.hours}")
                                println("start $start bt3")

                            } else {
                                binding.button3.isEnabled = true
                                binding.button3.text.toString()
                                println("2 true")
                                val deliveredTypeOne = binding.button3.text.toString()

                                val startDayss = Calendar.getInstance()
                                val day = 1

                                if (startDayss.time.day == 7) {
                                    val deliveredTypeOne = binding.button1.text.toString()
                                    startDayss.add(Calendar.DATE, day)
                                    deliveredTypeClickListener.invoke(uiModel.copy(deliveredTypeOne = "T3ND"))
                                    currentTimeDelivered.invoke(uiModel.copy(currentTimeDelivered = start.time.toString()))
                                } else {

                                    deliveredTypeClickListener.invoke(uiModel.copy(deliveredTypeOne = "T3SD"))
                                    currentTimeDelivered.invoke(uiModel.copy(currentTimeDelivered = start.time.toString()))
                                    println("start $start bt3 else")
                                }

                            }
                        }
                        R.id.button1nextDay -> {

                            val startDay1 = Calendar.getInstance()
                            val day = 1
                            val saturdayDay = 2
                            if (startDay1.time.day == 6) {

                                if (binding.button1nextDay.visibility == View.VISIBLE && binding.button2nextDay.visibility == View.VISIBLE && binding.button3nextDay.visibility == View.VISIBLE) {
                                    deliveredNextDay.invoke(uiModel)
                                }

                                val deliveredTypeOne = binding.button1nextDay.text.toString()
                                deliveredTypeClickListener.invoke(uiModel.copy(deliveredTypeOne = "T1ND"))

                                startDay1.add(Calendar.DATE, saturdayDay)
                                currentTimeDelivered.invoke(uiModel.copy(currentTimeDelivered = start.time.toString()))

                                println(" dia siguiente button1nextDay dia == 6::: ${startDay1.time}  ${startDay1.time.hours}")

                            } else {

                                val deliveredTypeOne = binding.button1nextDay.text.toString()

                                startDay1.add(Calendar.DATE, day)
                                deliveredTypeClickListener.invoke(uiModel.copy(deliveredTypeOne = "T1ND"))
                                currentTimeDelivered.invoke(uiModel.copy(currentTimeDelivered = start.time.toString()))

                            }
                        }
                        R.id.button2nextDay -> {
                            val startDay2 = Calendar.getInstance()

                            //println("  start.time.day::: ${startDay2.time.day}")

                            if (startDay2.time.day == 6) {

                                if (binding.button1nextDay.visibility == View.VISIBLE && binding.button2nextDay.visibility == View.VISIBLE && binding.button3nextDay.visibility == View.VISIBLE) {
                                    deliveredNextDay.invoke(uiModel)
                                }

                                val deliveredTypeOne = binding.button1nextDay.text.toString()
                                deliveredTypeClickListener.invoke(uiModel.copy(deliveredTypeOne = "T2ND"))
                                val saturdayDay: Int = 2
                                startDay2.add(Calendar.DATE, saturdayDay)

                                //println("  start.time.day::: after add ${startDay2.time.day}")
                                currentTimeDelivered.invoke(uiModel.copy(currentTimeDelivered = start.time.toString()))

                                //println(" dia siguiente button2nextDay dia == 6::: ${startDay2.time}  ${startDay2.time.hours}")

                            } else {

                                //println("  start.time.day ${startDay2.time.day}")

                                val deliveredTypeOne = binding.button1nextDay.text.toString()
                                deliveredTypeClickListener.invoke(uiModel.copy(deliveredTypeOne = "T2ND"))
                                val day: Int = 1
                                startDay2.add(Calendar.DATE, day)
                                currentTimeDelivered.invoke(uiModel.copy(currentTimeDelivered = start.time.toString()))
                                //println(" dia siguiente button2nextDay ::: ${startDay2.time}  ${startDay2.time.hours}")

                            }

                        }
                        R.id.button3nextDay -> {

                            val startDay3 = Calendar.getInstance()

                            val day: Int = 1
                            val saturdayDay: Int = 2

                            if (startDay3.time.day == 6) {

                                if (binding.button1nextDay.visibility == View.VISIBLE &&
                                    binding.button2nextDay.visibility == View.VISIBLE &&
                                    binding.button3nextDay.visibility == View.VISIBLE) {
                                    deliveredNextDay.invoke(uiModel)
                                }

                                val deliveredTypeOne = binding.button1nextDay.text.toString()
                                deliveredTypeClickListener.invoke(uiModel.copy(deliveredTypeOne = "T3ND"))

                                startDay3.add(Calendar.DATE, saturdayDay)
                                currentTimeDelivered.invoke(uiModel.copy(currentTimeDelivered = start.time.toString()))
                                //println(" dia siguiente button3nextDay dia == 6::: ${startDay3.time}  ${startDay3.time.hours}")

                            } else {

                                val deliveredTypeOne = binding.button1nextDay.text.toString()
                                deliveredTypeClickListener.invoke(uiModel.copy(deliveredTypeOne = "T3ND"))

                                startDay3.add(Calendar.DATE, day)
                                currentTimeDelivered.invoke(uiModel.copy(currentTimeDelivered = start.time.toString()))
                                //println(" dia siguiente button3nextDay ::: ${startDay3.time}  ${startDay3.time.hours}")

                            }
                        }
                    }

                }
            }


            fun typesComprove(types: ArrayList<String>?) {
                types?.map {
                    if (it == "ecommerce") {
                        binding.deliveryPriceText.visibility = View.VISIBLE
                        //binding.deliveryPriceTextSd.visibility = View.GONE
                        binding.deliveryPriceText.text = carriers.toString()
                        println(" carriersEco CartItemAdapter line 781 binding.deliveryPriceText.text ${binding.deliveryPriceText.text}   ")
                        println(" carriersEco CartItemAdapter line 782 $carriers")

                        println(
                            "  authStore.getCarriers().toString() ${
                                authStore.getCarriers().toString()
                            }"
                        )
                        binding.delieveryDayTextEco.visibility = View.VISIBLE
                        binding.radioGroup.visibility = View.GONE
                        binding.timeLabel.visibility = View.GONE
                    }
                    if (it == "sameday") {
                        //binding.delieveryDayTextEco.text = carriers.toString()
                        binding.deliveryPriceTextSd.visibility = View.VISIBLE
                        //binding.deliveryPriceText.visibility = View.GONE

                        println(" carriersSd.toString() $carriersSd   ")
                        binding.deliveryPriceTextSd.text = carriersSd.toString()

                        println(" carriersSd CartItemAdapter line 798 binding.deliveryPriceTextSd.text ${binding.deliveryPriceTextSd.text}   ")
                        println(" carriersSd CartItemAdapter line 799 $carriersSd")

                        println(
                            " carriersSd CartItemAdapter line 801 authStore.getCarriersSd().toString() ${
                                authStore.getCarriersSd().toString()
                            }"
                        )
                        binding.delieveryDayText.visibility = View.VISIBLE
                        binding.radioGroup.visibility = View.VISIBLE
                        binding.timeLabel.visibility = View.VISIBLE

                    }


                    //println(" typssssssssss $types it::::: $it")
                }

            }


            fun encrypt(
                cardNumber: String,
                expirDate: String,
                expireYear: String,
                securityCode: String,
                ownerName: String
            ): PaymentMethod {

                val cardNumber = cardNumber
                val ownerName = ownerName
                val expire = "20$expireYear"
                val cardType =
                    CardType.estimate(cardNumber)[0] // This is just an estimation and could be empty
                val expiryDate = ExpiryDate(expirDate.toInt(), expire.toInt())
                val securityCode = securityCode

                println(
                    "\n $cardNumber -- $expirDate " +
                            "-\n- $expireYear -- $securityCode " +
                            "-\n- $ownerName"
                )

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
                        BuildConfig.PUBLIC_KEY_MIS
                    )

                    paymentMethod = PaymentMethod(
                        encryptedCardNumber = encryptedCard.encryptedNumber.toString(),
                        encryptedExpiryMonth = encryptedCard.encryptedExpiryMonth.toString(),
                        encryptedExpiryYear = encryptedCard.encryptedExpiryYear.toString(),
                        encryptedSecurityCode = encryptedCard.encryptedSecurityCode.toString(),
                        encryptedUserName = "user name Default"
                    )
                    println(" tarjeta encryptada \n $encryptedCard")
                    println(" tarjeta encryptada.encryptedExpiryMonth \n ${encryptedCard.encryptedExpiryMonth}")
                    println(" tarjeta encryptada.encryptedExpiryYear \n ${encryptedCard.encryptedExpiryYear}")
                    println(" tarjeta encryptada.encryptedNumber \n ${encryptedCard.encryptedNumber}")
                    println(" tarjeta encryptada.encryptedSecurityCode \n ${encryptedCard.encryptedSecurityCode}")

                } else {
                    return PaymentMethod(
                        encryptedCardNumber = "0",
                        encryptedExpiryMonth = "0",
                        encryptedExpiryYear = "0",
                        encryptedSecurityCode = "0",
                        encryptedUserName = "user name Null"
                    )
                }
                return paymentMethod
            }


        }

        class ItemSubtotalViewHolder(
            private val binding: ItemCartSubtotalBinding
        ) : ListViewHolder(binding.root) {

            override fun bind(item: CartUiModel) {
                val uiModel = item as CartUiModel.Subtotal

                binding.priceText.text = "${String.format("%.2f", item.subtotal)} €"
                binding.productsLabelCartText.text = "(${uiModel.totalProducts})"
            }
        }

    }
        object ListDiffUtil : DiffUtil.ItemCallback<CartUiModel>(){
            override fun areItemsTheSame(oldItem: CartUiModel, newItem: CartUiModel): Boolean {
                return oldItem.uid == newItem.uid
            }

            override fun areContentsTheSame(oldItem: CartUiModel, newItem: CartUiModel): Boolean {
                return oldItem == newItem
            }
        }
}
