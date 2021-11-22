package com.miscota.android.api.autoship

import com.miscota.android.api.autoship.response.AutoShipResponse
import com.miscota.android.api.autoship.response.SendNowResponse
import okhttp3.RequestBody
import retrofit2.http.*

interface AutoShipApi {
    @GET("/app/user/{user_id}/autoship/get")
    suspend fun fetchAutoShipData(
        @Header("Authorization") authHeader: String,
        @Header("x-request-domain") requestDomain: String = "www.miscota.es",
        @Path("user_id") userId: Int,
    ): List<AutoShipResponse>

    @POST("/app/user/{user_id}/autoship/send")
    suspend fun forceAutoShipSend(
        @Header("Authorization") authHeader: String,
        @Header("x-request-domain") requestDomain: String = "www.miscota.es",
        @Path("user_id") userId: Int,
        @Body request: RequestBody
    ): List<SendNowResponse>

}