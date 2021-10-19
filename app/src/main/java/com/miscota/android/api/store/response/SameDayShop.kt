package com.miscota.android.api.store.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SameDayShop(
        @Json(name = "name")
        val name: String,
        @Json(name = "retail_shop_id")
        val retail_shop_id: String
)