package com.miscota.android.repository

import com.miscota.android.api.product.ProductApi
import com.miscota.android.models.ProductNetworkModel
import com.miscota.android.util.AuthStore

class ProductRepository(private val productApi: ProductApi, private val authStore: AuthStore) {

    suspend fun fetchProductsByCategory(
        categoryId: Int,
        page: Int,
        limit: Int,
        retailID: String,
        type: String,
    ): List<ProductNetworkModel> {
        return productApi.fetchProducts(
            authHeader = "Bearer " + authStore.getBearerToken(),
            categoryId = categoryId,
            page = page,
            limit = limit,
            requestRetailId = retailID,
            type = type
        )
    }

    suspend fun fetchProductById(
        productId: Long,
        type: String,
        retailID: String,
    ): List<ProductNetworkModel> {
        return productApi.fetchProduct(
            authHeader = "Bearer " + authStore.getBearerToken(),
            productId = productId,
            requestRetailId = retailID,
            type = type
        )
    }

    suspend fun searchProducts(
        query: String,
        type: String,
        retailID: String,
        position: Int,
        limit: Int
    ): List<ProductNetworkModel> {
        return productApi.searchProducts(
            authHeader = "Bearer " + authStore.getBearerToken(),
            query = query,
            requestRetailId = retailID,
            position = position,
            limit = limit,
            type = type
        )
    }
}