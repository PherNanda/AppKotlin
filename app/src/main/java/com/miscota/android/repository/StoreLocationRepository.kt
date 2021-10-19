package com.miscota.android.repository

import com.miscota.android.api.store.StoreLocationApi
import com.miscota.android.api.store.response.SameDayShop
import com.miscota.android.api.store.response.ShopLocationModel
import com.miscota.android.util.AuthStore

class StoreLocationRepository(
    private val storeLocationApi: StoreLocationApi,
    private val authStore: AuthStore
) {

    suspend fun getRetailers(): List<ShopLocationModel> {
        return storeLocationApi.fetchRetailers(
            authHeader = "Bearer " + authStore.getBearerToken()
        )
    }

    suspend fun getSameDayShops(postalCode: String): List<SameDayShop> {
        return storeLocationApi.checkSameDay(
                authHeader = "Bearer " + authStore.getBearerToken(),
                postalCode = postalCode
        )
    }

}