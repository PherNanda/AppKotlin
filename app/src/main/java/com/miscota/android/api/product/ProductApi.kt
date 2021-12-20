package com.miscota.android.api.product

import com.miscota.android.models.ProductNetworkModel
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ProductApi {

    @GET("/app/product/list/{type}/{category_id}/{position}/{limit}")
    suspend fun fetchProducts(
        @Header("Authorization") authHeader: String,
        @Header("x-request-domain") requestDomain: String = "www.miscota.es",
        @Header("x-request-retail-id") requestRetailId: String,
        @Path("type") type: String,
        @Path("category_id") categoryId: Int,
        @Path("position") page: Int,
        @Path("limit") limit: Int,
    ): List<ProductNetworkModel>

    @GET("/app/product/get/{type}/{product_id}")
    suspend fun fetchProduct(
        @Header("Authorization") authHeader: String,
        @Header("x-request-domain") requestDomain: String = "www.miscota.es",
        @Header("x-request-retail-id") requestRetailId: String,
        @Path("type") type: String,
        @Path("product_id") productId: Long,
    ): List<ProductNetworkModel>


    @GET("/app/product/search/{type}/{query}/")
    suspend fun searchProductsOld(
        @Header("Authorization") authHeader: String,
        @Header("x-request-domain") requestDomain: String = "www.miscota.es",
        @Header("x-request-retail-id") requestRetailId: String,
        @Path("type") type: String,
        @Path("query") query: String,
    ): List<ProductNetworkModel>

    @GET("/app/product/search_product/{type}/{position}/{limit}/{query}")
    suspend fun searchProducts(
        @Header("Authorization") authHeader: String,
        @Header("x-request-domain") requestDomain: String = "www.miscota.es",
        @Header("x-request-retail-id") requestRetailId: String,
        @Path("type") type: String,
        @Path("position") position: Int,
        @Path("limit") limit: Int,
        @Path("query") query: String,
    ): List<ProductNetworkModel>

}