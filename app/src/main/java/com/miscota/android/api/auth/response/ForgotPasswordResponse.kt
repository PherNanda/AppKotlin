package com.miscota.android.api.auth.response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ForgotPasswordResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "error") val error: String?,
    @Json(name = "send") val send: Boolean?,
)