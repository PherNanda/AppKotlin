package com.miscota.android.repository


import com.miscota.android.api.autoship.AutoShipApi
import com.miscota.android.api.autoship.response.AutoShipResponse
import com.miscota.android.api.autoship.response.SendNowResponse
import com.miscota.android.util.AuthStore
import okhttp3.MultipartBody

class AutoShipRepository(private val autoShipApi: AutoShipApi, private val authStore: AuthStore) {
    suspend fun fetchAutoShipData(userId: Int): List<AutoShipResponse> {
        return autoShipApi.fetchAutoShipData(
            authHeader = "Bearer " + authStore.getBearerToken(),
            userId = userId,
        )
    }

    suspend fun forceAutoShipSend(userId: Int, idAutoShip: Int): List<SendNowResponse> {

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("id_autoshipping", idAutoShip.toString())
            .build()

        return autoShipApi.forceAutoShipSend(
            authHeader = "Bearer " + authStore.getBearerToken(),
            userId = userId,
            request = requestBody,
        )
    }
}