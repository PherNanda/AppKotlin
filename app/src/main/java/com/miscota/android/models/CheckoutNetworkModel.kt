package com.miscota.android.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CheckoutNetworkModel(
    @Json(name = "ref_order") val ref_order: String?
)
