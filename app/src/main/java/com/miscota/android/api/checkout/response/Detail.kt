package com.miscota.android.api.checkout.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Detail(
    @Json(name = "details") val details: List<DetailX>,
    @Json(name = "items") val items: List<ItemX>,
    @Json(name = "key" ) val key: String,
    @Json(name = "optional") val optional: Boolean,
    @Json(name = "type") val type: String,
    @Json(name = "value") val value: String
)