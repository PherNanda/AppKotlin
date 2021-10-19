package com.miscota.android.api.checkout.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FraudCheckResult(
    @Json(name= "accountScore" ) val accountScore: Int,
    @Json(name= "checkId" ) val checkId: Int,
    @Json(name= "name" ) val name: String
)