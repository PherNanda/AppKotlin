package com.miscota.android.repository

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.miscota.android.BuildConfig
import com.miscota.android.api.checkout.CheckoutApi
import com.miscota.android.api.checkout.response.PaymentMethod
import com.miscota.android.models.CheckoutCarriers
import com.miscota.android.models.CheckoutNetworkModel
import com.miscota.android.models.Payment
import com.miscota.android.ui.cart.CartUiModel
import com.miscota.android.ui.checkoutpayment.PaymentRequestAdyen
import com.miscota.android.ui.checkoutpayment.PaymenthMethodsAdyen
import com.miscota.android.util.Address
import com.miscota.android.util.AuthStore
import com.miscota.android.util.UserModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject


class CheckoutRepository (private val checkoutApi: CheckoutApi,
                          private val authStore: AuthStore,
                          private val checkoutApiPayment: CheckoutApi) {

    suspend fun fetchCheckout(
        clientType: Boolean,
        user: UserModel,
        address: Address,
        email: String,
        newsLetter: Boolean,
        deliveryDate: String,
        deliveryRange: String,
        items: List<CartUiModel.ItemListCheckout>,
        shippingAddress: String,
        customAddress: Boolean,
        digitalDelivery: String,
        ecommerceShippingPrice: Double
    ): CheckoutNetworkModel {

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("name", user.name.toString())
            .addFormDataPart("surnames", user.surname.toString())
            .addFormDataPart("phone", user.phone.toString())
            .addFormDataPart("region", address.region.toString())
            .addFormDataPart("city", address.city)
            .addFormDataPart("postcode", address.postalCode)
            .addFormDataPart("address", address.address)
            .addFormDataPart("shipping_address",shippingAddress)
            .addFormDataPart("custom_address", customAddress.toString())
            .addFormDataPart("email", email)
            .addFormDataPart("source","45")
            .addFormDataPart("newsLetter", newsLetter.toString())
            .addFormDataPart("delivery_date",deliveryDate)
            .addFormDataPart("delivery_range", deliveryRange)
            .addFormDataPart("ecommerce_shipping_price",ecommerceShippingPrice.toString())
            .addFormDataPart("digital_delivery",digitalDelivery)
            .addFormDataPart("items", Gson().toJson(items))
            .build()

        return checkoutApi.fetchCheckout(
            authHeader = "Bearer " + authStore.getBearerToken(),
            requestRetailId = authStore.getRetailID().toString(),
            clientType = clientType,
            request = requestBody
        )
    }


    suspend fun fetchCarriersCheckout(
        requestRetailId: String,
        clientType: Boolean,
        items: List<CartUiModel.ItemListCheckout>
    ): CheckoutCarriers {

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("items", Gson().toJson(items))
            .build()

        return checkoutApi.fetchCarriersCheckout(
            authHeader = "Bearer " + authStore.getBearerToken(),
            requestRetailId = requestRetailId,
            carriersType = clientType,
            request = requestBody
        )
    }


    suspend fun fetchAdyenPaymentMethodCheckoutTest(
        paymentMethods: PaymenthMethodsAdyen,
    ): PaymentMethod {
        val requestBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("paymentMethods", Gson().toJson(paymentMethods))
        .build()
        return checkoutApi.fetchPaymentMethodTest(
            authHeader = BuildConfig.API_KEY_HEADER_NAME_TEST_MIS+ " " +BuildConfig.CHECKOUT_API_KEY_TEST_MIS,
            request = requestBody
        )
    }

    suspend fun fetchPaymentsAdyenTest(
        paymentRequest: PaymentRequestAdyen,
    ): Payment {

        val jsonObject = JSONObject()
        val amountArray = JsonArray()
        amountArray.add("currency")
        amountArray.add("value")
        val jsonObjectAmount = JSONObject()
        jsonObjectAmount.put("currency", paymentRequest.amount.currency)
        jsonObjectAmount.put("value", paymentRequest.amount.value)

        jsonObject.put("amount", amountArray)
        jsonObject.put("amount",jsonObjectAmount)
        jsonObject.put("merchantAccount", paymentRequest.merchantAccount)
        val paymentMethodArray = JsonArray()
        jsonObject.put("paymentMethod", paymentMethodArray)
        val jsonObjectPayment = JSONObject()
        jsonObjectPayment.put("encryptedCardNumber", paymentRequest.paymentMethod.encryptedCardNumber)
        jsonObjectPayment.put("encryptedExpiryMonth", paymentRequest.paymentMethod.encryptedExpiryMonth)
        jsonObjectPayment.put("encryptedExpiryYear", paymentRequest.paymentMethod.encryptedExpiryYear)
        jsonObjectPayment.put("encryptedSecurityCode", paymentRequest.paymentMethod.encryptedSecurityCode)
        jsonObjectPayment.put("type", paymentRequest.paymentMethod.type)
        jsonObject.put("paymentMethod", jsonObjectPayment)

        jsonObject.put("reference", paymentRequest.reference)
        jsonObject.put("returnUrl", paymentRequest.returnUrl)

        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

        return checkoutApi.fetchPaymentAdyenTest(
            authHeader = BuildConfig.CHECKOUT_API_KEY_TEST_MIS,
            request = requestBody
        )
    }


    suspend fun fetchPaymentAdyenPro(
        paymentRequest: PaymentRequestAdyen,
    ): Payment {
        val jsonObject = JSONObject()
        val amountArray = JsonArray()
        amountArray.add("currency")
        amountArray.add("value")
        val jsonObjectAmount = JSONObject()
        jsonObjectAmount.put("currency", paymentRequest.amount.currency)
        jsonObjectAmount.put("value", paymentRequest.amount.value)

        jsonObject.put("amount", amountArray)
        jsonObject.put("amount",jsonObjectAmount)
        jsonObject.put("reference", paymentRequest.reference)
        val paymentMethodArray = JsonArray()
        jsonObject.put("paymentMethod", paymentMethodArray)
        val jsonObjectPayment = JSONObject()
        jsonObjectPayment.put("type", paymentRequest.paymentMethod.type)
        jsonObjectPayment.put("encryptedCardNumber", paymentRequest.paymentMethod.encryptedCardNumber)
        jsonObjectPayment.put("encryptedExpiryMonth", paymentRequest.paymentMethod.encryptedExpiryMonth)
        jsonObjectPayment.put("encryptedExpiryYear", paymentRequest.paymentMethod.encryptedExpiryYear)
        jsonObjectPayment.put("encryptedSecurityCode", paymentRequest.paymentMethod.encryptedSecurityCode)
        jsonObject.put("paymentMethod", jsonObjectPayment)

        jsonObject.put("returnUrl", paymentRequest.returnUrl)
        jsonObject.put("merchantAccount", paymentRequest.merchantAccount)

        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

        return checkoutApi.fetchPaymentAdyenPro(
            authHeader = BuildConfig.CHECKOUT_API_KEY_MIS,
            request = requestBody
        )
    }

}