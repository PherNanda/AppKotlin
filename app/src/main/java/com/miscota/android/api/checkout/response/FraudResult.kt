package com.miscota.android.api.checkout.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FraudResult(
    @Json(name = "accountScore" ) val accountScore: Int,
    @Json(name = "results" ) val results: List<Result>
)