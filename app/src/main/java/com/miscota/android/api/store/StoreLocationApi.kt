package com.miscota.android.api.store

import com.miscota.android.api.store.response.SameDayShop
import com.miscota.android.api.store.response.ShopLocationModel
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface StoreLocationApi {

    @GET("/retail/shops")
    suspend fun fetchRetailers(
        @Header("Authorization") authHeader: String,
    ): List<ShopLocationModel>

    @GET("/app/shop/list/{postal_code}")
    suspend fun checkSameDay(
            @Header("Authorization") authHeader: String,
            @Header("x-request-domain") requestDomain: String = "www.miscota.es",
            @Path("postal_code") postalCode: String
    ): List<SameDayShop>
}