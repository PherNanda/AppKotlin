package com.miscota.android.repository

import com.miscota.android.api.category.CategoryApi
import com.miscota.android.models.CategoryNetworkModel
import com.miscota.android.util.AuthStore

class CategoryRepository(private val categoryApi: CategoryApi, private val authStore: AuthStore) {

    suspend fun fetchCategories(productType: String, requestDetail: String): List<CategoryNetworkModel> {
        return categoryApi.fetchCategories(
            authHeader = "Bearer " + authStore.getBearerToken(),
            requestRetailId= requestDetail,
            productType = productType,
        )
    }

}