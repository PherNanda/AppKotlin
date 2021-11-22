package com.miscota.android.api.checkout

import com.google.gson.JsonObject
import com.miscota.android.BuildConfig
import com.miscota.android.api.checkout.response.PaymentMethod
import com.miscota.android.models.CheckoutCarriers
import com.miscota.android.models.CheckoutNetworkModel
import com.miscota.android.models.Payment
import okhttp3.RequestBody
import retrofit2.http.*

interface CheckoutApi {

        @POST("/app/cart/checkout/{client-type}")
        suspend fun fetchCheckout(
            @Header("Authorization") authHeader: String,
            @Header("x-request-domain") requestDomain: String = "www.miscota.es",
            @Header("x-request-retail-id") requestRetailId: String = "0",
            @Path("client-type") clientType: Boolean,
            @Body request: RequestBody
        ): CheckoutNetworkModel

        @POST("/app/cart/carriers/{client-type}")
        suspend fun fetchCarriersCheckout(
            @Header("Authorization") authHeader: String,
            @Header("x-request-domain") requestDomain: String = "www.miscota.es",
            @Header("x-request-retail-id") requestRetailId: String,
            @Path("client-type") carriersType: Boolean,
            @Body request: RequestBody
        ): CheckoutCarriers

        @Headers("Content-Type: application/json")
        @POST(BuildConfig.PAYMENT_URL_TEST_MIS)
        suspend fun fetchPaymentAdyenTest(
            @Header(BuildConfig.API_KEY_HEADER_NAME_TEST_MIS) authHeader: String,
            @Header("merchantAccount") merchantAccount: String = BuildConfig.MERCHANT_ACCOUNT_TEST_MIS,
            @Body request: RequestBody
        ): Payment

        @Headers("Content-Type: application/json")
        @POST(BuildConfig.PAYMENT_URL_MIS)
        suspend fun fetchPaymentAdyenPro(
            @Header(BuildConfig.API_KEY_HEADER_NAME_MIS) authHeader: String,
            @Header("merchantAccount") merchantAccount: String = BuildConfig.MERCHANT_ACCOUNT_MIS,
            @Body request: RequestBody
        ): Payment


    //producción
    @POST("https://4b3d6e25656fcd70-Miscota-checkout-live.adyenpayments.com/checkout/v67/paymentMethods")
    suspend fun fetchAdyenPaymentMethodCheckout(
        @Header("Authorization") authHeader: String,
        @Header("merchantAccount") requestRetailId: String = "MiscotaCOM",
        @Path("client-type") carriersType: Boolean,
        @Body request: RequestBody
    ): CheckoutCarriers


    @POST("https://checkout-test.adyen.com/v66/paymentMethods")
    suspend fun fetchPaymentMethodTest(
        @Header("Authorization") authHeader: String,
        @Header("merchantAccount") requestRetailId: String = "MiscotaCOM",
        @Body request: RequestBody
    ): PaymentMethod


    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("https://checkout-test.adyen.com/v66/payments")
    suspend fun fetchPaymentAdyenTest(
        @Header("Authorization") authHeader: String,
        @Header("merchantAccount") requestRetailId: String = "MiscotaCOM",
        @Body request: JsonObject
    ): Payment

}