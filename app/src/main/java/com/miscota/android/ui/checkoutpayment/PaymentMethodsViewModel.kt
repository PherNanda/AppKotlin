package com.miscota.android.ui.checkoutpayment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adyen.checkout.card.CardValidationUtils
import com.adyen.checkout.card.data.CardType
import com.adyen.checkout.card.data.ExpiryDate
import com.adyen.checkout.core.log.LogUtil
import com.adyen.checkout.core.log.Logger
import com.adyen.checkout.cse.Card
import com.adyen.checkout.cse.Encryptor
import com.miscota.android.R
import com.miscota.android.repository.CheckoutRepository
import com.miscota.android.ui.cart.CartUiModel
import com.miscota.android.util.AuthStore
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class PaymentMethodsViewModel (
    private val authStore: AuthStore, private val checkoutRepository: CheckoutRepository
) : ViewModel() {

    companion object {
        private val TAG = LogUtil.getTag()
    }

    private val parentJob = Job()

    private val coroutineContext: CoroutineContext get() = parentJob + Dispatchers.Main

    private val scope = CoroutineScope(coroutineContext)

    private val _checkoutResult = MutableLiveData<CheckoutResult>()
    val checkoutResult: LiveData<CheckoutResult> = _checkoutResult

    lateinit var paymentMethod: PaymentMethod

    private val PUBLIC_KEY = "10001|E56D388FC118357D813AC3B5FC1AB56E9E9924130D5490F81ECB3C4E4B148128CBB56D0C9265C826309C389DC804660D5A86CE19065230F3801A2ABD196E19B1F7F499B439AE525290EA73F7A65AAB89F7DC3D56A286647CC668DA6585A6F9ECFCBF7630C60551894F8309BB0E9A76E88ACC5199881058EB6F89C00BA9E5DC099ED96F0425853B3DD1032B461198F705C5291AE664E4FD2E151F81B85F8929C805387AE6608119E8C4741783377ABE1FF6F68A320C603E3723D657019E6623D0C020AB33D1F472C4E90198920ACFAE3BEE33CEE6F5FDA30B62F738B892C60A2A6B0F620FB29450869A889B578F21821628DABFC9263F2584C71421B04784DDA5"
    private val PAYMENT_METHOD_URL = "https://checkout-test.adyen.com/v66/paymentMethods"
    private val PUBLIC_KEY_MY_TEST = "10001|C3DF29152A68455D1E58354CAC4B7F94AD2CB3D1189A492D45B5E3190ECF5D95326D21922F597D99DD574AC255741B90901DB8D024F3F7528C3AD39EF686F9040F6279E68631C7C48258DFB678A0BACD51980C7F79A9F62FA8688381069EA7AF76EBBF32F3C58EB8DA6B79EA97AF8249EE1AC5DD6C05AA1B086AD82209636D9120A51F9D9E0AB6522717C8DA439D1E7C80AB6A205210C73531E1A239E2F438BE45BEB6BE105DE981CFD43B0816C0B9647475A2607B84DE166D4B34025C89D1CBAB2C1B8F848ED90B623DDA9BCD221953E23368AD947255ADE531EC81413060152161B07533017355F6D6D4C464EB97EAA35BD2F08D60D6774A854CCEC43620EF"

    fun requestPaymentMethods() {
        scope.launch {
                    //getPaymentMethodRequest()
        }
    }

    /**private fun getPaymentMethodRequest(): PaymentMethodsRequest {
        return PaymentMethodsRequest(
            BuildConfig.MERCHANT_ACCOUNT_TEST_MIS,
            BuildConfig.SHOPPER_REFERENCE_TEST_MIS,
            Amount("EUR", getTotalPriceAdyen(priceIn: String)), "ES",
            "es_ES",
            "606123456",
            "fernanda.leal@buddy.eu",
            paymentMethod
        )
    }**/


    fun getTotalPriceAdyen(priceIn: String): Int{
        var price = priceIn.split(" ").toTypedArray()
        var allPrice = price[0].replace(".","")
        var priceToAdyen = allPrice.toInt()*100
        println(" priceeeeee cartview ${price[0].replace(".","")}")
        return priceToAdyen
    }

    override fun onCleared() {
        super.onCleared()
        Logger.d(TAG, "onCleared")
        coroutineContext.cancel()
    }

    fun encrypt(
        cardNumber: String,
        expiryDate: String,
        expireYear: String,
        securityCode: String,
        ownerName: String
    ): PaymentMethod {

        val cardNumber = cardNumber
        val ownerName = ownerName
        val cardType =
            CardType.estimate(cardNumber)[0] // This is just an estimation and could be empty
        val expiryDate = ExpiryDate(expiryDate.toInt(), 2030)
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

            val encryptedCard = Encryptor.INSTANCE.encryptFields(
                rawCardData,
                PUBLIC_KEY
            )

            paymentMethod = PaymentMethod(
            encryptedCardNumber = encryptedCard.encryptedNumber.toString(),
            encryptedExpiryMonth = encryptedCard.encryptedExpiryMonth.toString(),
            encryptedExpiryYear = encryptedCard.encryptedExpiryYear.toString(),
            encryptedSecurityCode = encryptedCard.encryptedSecurityCode.toString(),
            encryptedUserName = ownerName
            )

           /** paymentMethod = PaymentMethod(
                encryptedCardNumber = "adyenio_0_1_25\$RusFOFSGf8KyjPKEEQFW01HLJnJeagnDY3CKqafyUAWsvxXAtsvcaj76OzgrGUd7JZ8wUHOM7iKJXHT0zn9F+IrHHXaijXX6k/0Tg5FXMmcNGijHXjpv9MhwxdI3wcUXvjqdyZk4WHQP9ZuiqZ9yjJ4FOYvs37B3gsp5QUDYrEMVjXCNtXVJForVcGwMlHayx6yr4AcIZUt/qK4pBYQJqyIGVhQC4BXk2MzJJKYO1ih9oeTn1+rG+Enpbl2yzFcBVmUrpyP55czp30zxwqrvxbzXCgkqCq2biK5m7esudzN7WNWWp0o9NjPrAJl+QVV3oKxBmHEQJNDtbwusbtO6vg==\$/hL3V87dWKVGwMPUzNCUFw5FH0Vwhm4JaRiS5md7zJVCmNJ2GgHMLkhVGOI+WSa12H1cuaApO1mZPDWJx4miVwiE53RapXyHzQVZBFGy1LZERRx2kJiEnvQ=",
                encryptedExpiryMonth = "adyenio_0_1_25\$i6CeaMs2D6EwhlV0APDYtYyEuB+FMomUvvNAk0aUGczjfIo/lcBnGyKSVsbO1v3udHAIfWkpzfFLvfNXY4PJW99a9rv+CX7jyNJXb2E+7Q5JRH9/lZaUEKnHnpcl4CwheNm7WIrZwwiMJZ1mTW/GqmJ93t2hPHC20nZiZn//+ODnomXmWrMCcnQVoQYXzqLULePKJr4VEwwigy9sY06/ywi2DQ3HAInupjUO2bZJjdTw93gs3KrqvzgDlmghxr3xdKLN4yGy1EbuA32upnZuGy2KGYy7GIgURJ5p+ClzoJcy3AqT9g29khCPlHLCXgH1etXujtRMDP9HfSvg3sI30Q==\$MjRsYv4dTGAxWuqBA0ROin+dyDtAgJSLWNXzG/eqaXyVFGNiubIoMpuSBBgZjMb1cvYS0SeTIWBBpvHYWgQwutX/YRmJ4FfuTpPFLcsvzVA=",
                encryptedExpiryYear = "adyenio_0_1_25\$Wuy84T43eqB1mlNPvb3yrW9K8dtBSWzJrJ6aOInNGuWLrdtwn6UkplJgBfWmpEBTKLAjN2u6nzKo40zhSWq9/pDRY43ZXUjGHnvtzWOIQm/IRZJY4WZlr1sdF74DIHXdpqqoK054zDcuY5Rr4RkZROkR2zdK6WTpXhDzqiAnAUguRnqVo8rx1kPoKwf8Vsh3pf0e33LP6lP8iLsqMsxae0I40KDEmBrq8Hm+xvkI9hP1tMghn8DWnRZU1MvTQJpiXeCJn9lVLk8S6PY9WDmenQdJmxfJu21HMpYEVueGfQgIKpvmfB+MUqGotHCXktU4kKK3IYcUdSOGdr0gpfZ/1w==\$fQQ1tmdE6CO98NPVGCOuqZSsexZOuJ1xSiNtRkfj+gAUcTNdt6SwcWcm3Mdf/b5ECv/5u+QIJpDA9B5LwRj2NfUtqgF2TQagk2Qk6Kn442kc",
                encryptedSecurityCode = "adyenio_0_1_25\$V34lO305Wp+VkggYXlabXhTeexgGi+NvI/nhE9Oa2IrzBysz87mFvFB/DOCitzmMHg6qsN39HE7PI0Sn+Zpf6DD7IbwaqIA+k73RqZzL6ugdK3BkVI5JMP7B0NfgZGQZHvNJvwVKt8V1SaGpI8Dz5HQtyHnU6Zeif7MrHPsXDMzK4riwt+rc2L8RWZgF4CS030Xu32Z807MhoDSkglkb5Vn4WJEpp4WrOt04fcm6FRuEjE9BjGXqVzIz3qwB7kprSXqlZFNL0fR/A+HlWNvp+42jD+ZXHgRQ1AGLbbU0d+zDvBy6rMbFzHXYyNO787u8jobTxcgi2KSYQMMVpE5HmQ==\$TVroYnKTC6szwwrFmM8hCGPCaXdwcEWEZBtb1PjWZwB2HODEGNc5qxUlyPaC5051kKCmaqX+H7Fc8e/V3tQPTyFHkYJD46l8KQ=="
            )**/

            /**println(" tarjeta encryptada \n $encryptedCard")
            println(" tarjeta encryptada.encryptedExpiryMonth \n ${encryptedCard.encryptedExpiryMonth}")
            println(" tarjeta encryptada.encryptedExpiryYear \n ${encryptedCard.encryptedExpiryYear}")
            println(" tarjeta encryptada.encryptedNumber \n ${encryptedCard.encryptedNumber}")
            println(" tarjeta encryptada.encryptedSecurityCode \n ${encryptedCard.encryptedSecurityCode}")**/

        }

        return paymentMethod

    }


    fun checkout(clientType: Boolean, itens: List<CartUiModel.ItemListCheckout>) {
        viewModelScope.launch {
            val result = runCatching {
                checkoutRepository.fetchCheckout(
                    clientType = clientType,
                    user = authStore.getUser()!!,
                    address = authStore.getAddress()!!,
                    email = authStore.getEmail()!!,
                    newsLetter = true,
                    deliveryDate = "2021-06-01",
                    deliveryRange = "T1SD",
                    items = itens,
                    shippingAddress = "",
                    customAddress = false,
                    digitalDelivery = "",
                    ecommerceShippingPrice = 5.99
                )
            }

            if (result!=null) {

                _checkoutResult.value =
                    CheckoutResult(success = "chekout Ok"?:" -- ")
            } else {
                _checkoutResult.value = CheckoutResult(error = R.string.checkout_failed?:0)
            }
        }
    }


    fun fetchAdyenPaymentMethodCheckoutTest(paymentMethods: PaymenthMethodsAdyen) {
        viewModelScope.launch {
            val result = runCatching {
                checkoutRepository.fetchAdyenPaymentMethodCheckoutTest(
                    paymentMethods = paymentMethods
                )
            }

            if (result!=null) {

                _checkoutResult.value =
                    CheckoutResult(success = "chekout Ok"?:" -- ")
                println("  paymentMethods:: ${result.getOrThrow().name}")
            } else {
                _checkoutResult.value = CheckoutResult(error = R.string.checkout_failed?:0)
            }
        }
    }

    fun fetchPaymentTest(paymentRequest: PaymentRequestAdyen) {
        viewModelScope.launch {
            val result = runCatching {
                checkoutRepository.fetchPaymentsAdyenTest(
                    paymentRequest = paymentRequest
                )
            }

            if (result!=null) {

                _checkoutResult.value =
                    CheckoutResult(success = "chekout Ok"?:" -- ")
                println(" result s: ${result.isSuccess}")
                println(" result f: ${result.isFailure}")
                println(" result exceptionOrNull: ${result.exceptionOrNull()}")
                println("  resultCode:: ${result.getOrThrow().resultCode}")
                println("  pspReference:: ${result.getOrThrow().pspReference}")
            } else {
                _checkoutResult.value = CheckoutResult(error = R.string.checkout_failed?:0)
            }
        }
    }



}
