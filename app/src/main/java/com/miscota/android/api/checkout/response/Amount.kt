package com.miscota.android.api.checkout.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Amount(
    @Json(name = "currency" ) val currency: String,
    @Json(name = "value" ) val value: Int
)