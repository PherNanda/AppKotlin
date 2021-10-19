package com.miscota.android.api.checkout.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PaymentResponse(
    @Json(name = "amount" ) val amount: Amount,
    @Json(name = "fraudResult" ) val fraudResult: FraudResult?,
    @Json(name = "fraudResult" ) val merchantReference: String,
    @Json(name = "merchantReference" ) val pspReference: String,
    @Json(name = "resultCode" ) val resultCode: String
)
