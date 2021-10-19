package com.miscota.android.ui.checkoutpayment

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PaymentRequestAdyen(
    @Json(name = "amount" ) val amount: Amount,
    @Json(name = "merchantAccount" ) val merchantAccount: String = "MiscotaCOM",
    @Json(name = "paymentMethod" ) val paymentMethod: PaymentMethod,
    @Json(name = "reference" ) val reference: String,
    @Json(name = "returnUrl" ) val returnUrl: String = "miscota://paymentResult"
)