package com.miscota.android.ui.category

import com.miscota.android.models.ProductNetworkModel
import java.util.*


sealed class CategoryUiModel(
    open val uid: Long = UUID.randomUUID().mostSignificantBits
) {

    object LoaderItem : CategoryUiModel()

    object SpacerItem : CategoryUiModel()

    object TextItem : CategoryUiModel()

    data class CategoryListItem(
        override val uid: Long = UUID.randomUUID().leastSignificantBits,
        val categories: List<Category>
    ) : CategoryUiModel() {
        data class Category(
            val uid: Long = UUID.randomUUID().leastSignificantBits,
            val title: String,
            val isChecked: Boolean,
        )
    }

    data class TopProductListItem(
        override val uid: Long = UUID.randomUUID().leastSignificantBits,
        val products: List<Product>
    ) : CategoryUiModel()

    data class Combination(
        val idProduct: Int?,
        val ref: String?,
        val price: Double?,
        val variation: String?,
        var stock: Int?,
        val unitsPack: Int?,
        val stockItem: Int?,
        val productTypeOption: String?,
        val isChecked: Boolean?
    )

    data class Product(
        override val uid: Long = UUID.randomUUID().leastSignificantBits,
        val productId: Long,
        val productName: String,
        val description: String,
        val descr: String,
        val productPrice: String,
        val discountPrice: String,
        val brand: String,
        val discount: String,
        val hasDiscount: Boolean,
        val imageList: List<String?>,
        val combinations: List<Combination>,
        val productType: String?,
        val variations: String?
    ) : CategoryUiModel()
}


fun ProductNetworkModel.toCategoryCombinationsUiModel(): List<CategoryUiModel.Combination> {
    return combinations.map {
        CategoryUiModel.Combination(
            idProduct = it.idProduct,
            ref = it.ref,
            price = it.price,
            variation = it.variation,
            stock = it.stock,
            unitsPack = it.unitsPack,
            stockItem = it.stock,
            productTypeOption = this.productType?:"",
            isChecked = false
        )
    } ?: listOf()
}

fun ProductNetworkModel.toCategoryProductUiModel(): CategoryUiModel.Product {
    return CategoryUiModel.Product(
        productId = idProduct.toLong(),
        productName = title,
        productPrice = priceText,
        discountPrice = priceText,
        hasDiscount = false,
        imageList = listOf(
            thumbImg,
            thumbImg2,
            thumbImg3,
            thumbImg4,
            thumbImg5,
            thumbImg6,
            thumbImg7,
            thumbImg8,
            thumbImg9
        ),
        description = shortDescr ?: "",
        descr = descr?:"",
        discount = "0",
        brand = brand,
        combinations = toCategoryCombinationsUiModel(),
        productType = productType,
        variations = variations
    )
}

fun ProductNetworkModel.toBrandsUiModel(): CategoryUiModel.CategoryListItem.Category {
    return CategoryUiModel.CategoryListItem.Category(
        title = brand,
        isChecked = false,
    )
}

