package com.miscota.android.ui.productdetail

import android.os.Parcelable
import com.miscota.android.ui.category.CategoryUiModel
import com.miscota.android.util.CurrencyUtils
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Product(
        val productId: Long,
        val imageList: List<String?>,
        val productTitle: String,
        val productDescription: String,
        val price: String,
        val oldPrice: String,
        val discount: String,
        val hasDiscount: Boolean,
        val combinationOptions: List<OptionUiModel.Option>,
        var productType: String?,
        val brand: String,
        val descr: String?
) : Parcelable

sealed class OptionUiModel(
        val uid: Long = UUID.randomUUID().leastSignificantBits,
){
    @Parcelize
    data class Option(
        val id: String,
        val variant: String,
        val price: String,
        val isChecked: Boolean,
        val optionPrice: Double,
        var stock: Int?,
        val unitsPack: Int?,
        var productTypeOption: String?,
        var stockItens: Int?
    ) : OptionUiModel(), Parcelable

    object Spacer: OptionUiModel()
}

@JsonClass(generateAdapter = true)
data class CartProduct(
    @Json(name = "id_product") val productId: Int,
    @Json(name = "title") val title: String,
    @Json(name = "image") val image: String,
    @Json(name = "price") val price: String,
    @Json(name = "oldPrice") val oldPrice: String,
    @Json(name = "discount") val discount: String,
    @Json(name = "combinationReference") val combinationReference: String,
    @Json(name = "combinationPrice") val combinationPrice: Double,
    @Json(name = "stock") val combinationStock: Int?,
    @Json(name = "units_pack") val combinationUnitsPack: Int,
    @Json(name = "type") val typeProduct: String,
    @Json(name = "stockItens") val stockItens: Int?,
    @Json(name = "brand") val brand: String,
    @Json(name = "costSd") val costSd: Double,
    @Json(name = "costEco") val costEco: Double,
    @Json(name = "totalCost") val totalCost: Double,
)

fun CategoryUiModel.Product.toProductDetailUiModel(): Product {
    return Product(
            productId = productId,
            imageList = imageList,
            productTitle = productName,
            productDescription = description,
            price = productPrice,
            discount = discount,
            oldPrice = discountPrice,
            hasDiscount = hasDiscount,
            combinationOptions = combinationsToOptionsUiModel(),
            productType = productType ,
            brand = brand,
            descr = descr
    )
}

fun CategoryUiModel.Product.combinationsToOptionsUiModel(): List<OptionUiModel.Option> {
    return combinations.mapIndexed { index, combination ->
        OptionUiModel.Option(
            id = combination.ref ?: "",
            variant = combination.variation ?: "",
            price = CurrencyUtils.formatPrice(combination.price),
            isChecked = if (index == 0) true else false,
            optionPrice = combination.price ?: 0.0,
            stock = combination.stock,
            unitsPack = combination.unitsPack ?: 0,
            productTypeOption = productType,
            stockItens = combinations.get(index).stockItem
        )
    }

}