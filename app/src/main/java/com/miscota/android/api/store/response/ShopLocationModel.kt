package com.miscota.android.api.store.response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ShopLocationModel(
    @Json(name = "City")
    val city: String,
    @Json(name = "CompanyCIF")
    val companyCIF: String,
    @Json(name = "CompanyName")
    val companyName: String,
    @Json(name = "CostCenter")
    val costCenter: Any?,
    @Json(name = "Country")
    val country: String,
    @Json(name = "CurrencyCode")
    val currencyCode: String,
    @Json(name = "DistrivetId")
    val distrivetId: Any?,
    @Json(name = "PostalCode")
    val postalCode: String,
    @Json(name = "Province")
    val province: String,
    @Json(name = "ShopAddress")
    val shopAddress: String,
    @Json(name = "ShopCIF")
    val shopCIF: String,
    @Json(name = "ShopEmail")
    val shopEmail: String,
    @Json(name = "ShopId")
    val shopId: String,
    @Json(name = "ShopName")
    val shopName: String,
)