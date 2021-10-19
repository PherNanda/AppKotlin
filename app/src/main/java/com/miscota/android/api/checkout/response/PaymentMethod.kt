package com.miscota.android.api.checkout.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PaymentMethod(
    @Json(name = "brands") val brands: List<String>,
    @Json(name = "configuration") val configuration: Configuration,
    @Json(name = "details") val details: List<Detail>,
    @Json(name = "name") val name: String,
    @Json(name = "type") val type: String
)