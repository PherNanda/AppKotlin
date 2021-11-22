package com.miscota.android.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Payment (
    @Json(name = "amount") val amount: Amount,
    @Json(name = "fraudResult")  val fraudResult: FraudResult?,
    @Json(name = "merchantReference") val merchantReference: String,
    @Json(name = "pspReference") val pspReference: String,
    @Json(name = "resultCode") val resultCode: String
)

@JsonClass(generateAdapter = true)
data class Amount(
    @Json(name = "currency") val currency: String,
    @Json(name = "value") val value: Int
)

@JsonClass(generateAdapter = true)
data class FraudResult(
    @Json(name = "accountScore") val accountScore: Int,
    @Json(name = "results") val results: List<Result>
)

@JsonClass(generateAdapter = true)
data class FraudCheckResult(
    @Json(name = "accountScore") val accountScore: Int,
    @Json(name = "checkId") val checkId: Int,
    @Json(name = "name") val name: String
)

@JsonClass(generateAdapter = true)
data class Result(
    @Json(name = "FraudCheckResult") val FraudCheckResult: FraudCheckResult
)

@JsonClass(generateAdapter = true)
data class StatusResult(
    @Json(name = "errorCode" )val errorCode: String,
    @Json(name = "errorType" )val errorType: String,
    @Json(name = "message" ) val message: String,
    @Json(name = "status" )val status: Int
)

