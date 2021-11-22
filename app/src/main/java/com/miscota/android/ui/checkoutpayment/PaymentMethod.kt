package com.miscota.android.ui.checkoutpayment

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PaymentMethod(
    @Json(name = "type" ) val type: String = "scheme",
    @Json(name = "encryptedCardNumber" ) val encryptedCardNumber: String,
    @Json(name = "encryptedExpiryMonth" ) val encryptedExpiryMonth: String,
    @Json(name = "encryptedExpiryYear" ) val encryptedExpiryYear: String,
    @Json(name = "encryptedSecurityCode" ) val encryptedSecurityCode: String,
    @Json(name = "encryptedUserName" ) val encryptedUserName: String,
)