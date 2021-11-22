package com.miscota.android.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CategoryNetworkModel(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "categories") val categories: List<CategoryNetworkModel>,
)
