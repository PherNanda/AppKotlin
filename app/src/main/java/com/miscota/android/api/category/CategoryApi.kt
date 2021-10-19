package com.miscota.android.api.category

import com.miscota.android.models.CategoryNetworkModel
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface CategoryApi {

    //@GET("/app/categories/list/sameday")
    @GET("/app/categories/list/{product-type}")
    suspend fun fetchCategories(
        @Header("Authorization") authHeader: String,
        @Header("x-request-domain") requestDomain: String = "www.miscota.es",
        @Header("x-request-retail-id") requestRetailId: String,
        @Path("product-type") productType: String,
    ): List<CategoryNetworkModel>

}