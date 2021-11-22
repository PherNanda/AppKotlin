package com.miscota.android.ui.store

import android.location.Geocoder
import com.miscota.android.api.store.response.SameDayShop
import com.miscota.android.api.store.response.ShopLocationModel
import com.miscota.android.ui.category.CategoryUiModel
import com.miscota.android.ui.productdetail.Product
import com.miscota.android.ui.productdetail.combinationsToOptionsUiModel
import com.squareup.moshi.Json
import java.util.*
import kotlin.collections.ArrayList

data class StoreLocationUiModel(
    val id: Long = UUID.randomUUID().mostSignificantBits,
    val addressTitle: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val isSelected: Boolean,
)

data class Store(
    val name: String,
    val retail_shop_id: String
)

fun StoreLocationUiModel.toStoreUiModel(listStore: List<SameDayShop>): ArrayList<Store>? {
    val listStoreReturn: ArrayList<Store>? = null
    listStore.map {
         val store = Store(

            name = it.name,
            retail_shop_id = it.retail_shop_id
        )
        listStoreReturn?.addAll(listOf(store))
    }
    return listStoreReturn
}

fun ShopLocationModel.toStoreLocationUiModel(geocoder: Geocoder): StoreLocationUiModel? {
    val location = geocoder.getFromLocationName(shopAddress, 1)

    if (location.isEmpty()) return null

    val latitude = location[0].latitude
    val longitude = location[0].longitude
    return StoreLocationUiModel(
        addressTitle = shopName,
        address = location[0].getAddressLine(0) ?: "",
        isSelected = false,
        latitude = latitude,
        longitude = longitude,
    )
}
