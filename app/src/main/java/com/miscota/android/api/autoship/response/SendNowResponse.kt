package com.miscota.android.api.autoship.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SendNowResponse (
    @Json(name = "response") val response: List<Status>,
) {
    @JsonClass(generateAdapter = true)
    data class Status(
        @Json(name = "result") val result: Boolean,
        @Json(name = "status") val status: Boolean,
    )
}