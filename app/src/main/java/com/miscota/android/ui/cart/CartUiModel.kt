package com.miscota.android.ui.cart

import com.miscota.android.util.Address
import com.miscota.android.util.CartItem
import java.util.*
import kotlin.collections.ArrayList

sealed class CartUiModel(
    open val uid: Long = UUID.randomUUID().mostSignificantBits
) {

    object LoaderItem : CartUiModel()

    object SpacerItem : CartUiModel()

    data class  SummaryItem(
        val address: Address?,
        val deliveredTypeOne: String?,
        val currentTimeDelivered: String?,
        val email: String?,
        val phone: String?,
        val typeOne: ArrayList<String>?,
        val payment: com.miscota.android.ui.checkoutpayment.PaymentMethod?,
        val costSd: Double,
        val costEco: Double,
        val totalCost: Double
    ) : CartUiModel()

    data class Item(
        override val uid: Long = UUID.randomUUID().leastSignificantBits,
        val productId: Int,
        val productName: String,
        val productPrice: String,
        val oldPrice: String,
        val image: String,
        val quantity: Int,
        val discount: String,
        val stock: Int,
        val type: String?,
        val reference: String,
        val price: Double,
        val brand: String,
        val costSd: Double,
        val costEco: Double,
        val totalCost: Double,
        val samedayDelivery: String?
    ) : CartUiModel()

    data class ItemListCheckout(
        val qty: String,
        val price: String,
        val type: String?,
        val ref: String
    )

    data class MoreInfoCheckout(
        val guess: Boolean,
        val stock: Int,
        val ref: String,
        val rangeDelivery: String,
        val productType: String
    )

    data class RefOrderCheckout(
        val refOrder: String?
    )

    data class Subtotal(
        override val uid: Long = UUID.randomUUID().leastSignificantBits,
        val subtotal: Double,
        val totalProducts: Int
    ) : CartUiModel()

}

fun CartItem.toCartUiModel(): CartUiModel.Item {
    return CartUiModel.Item(
        productId = this.productId,
        productName = this.product.title,
        productPrice = this.product.price,
        oldPrice = this.product.oldPrice,
        image = this.product.image,
        quantity = this.qty,
        discount = this.product.discount,
        stock = this.stock ?: 0,
        type = this.type,
        reference = this.combinationReference,
        price = this.combinationPrice,
        brand = this.product.brand,
        costSd = this.product.costSd,
        costEco = this.product.costEco,
        totalCost = this.product.totalCost,
        samedayDelivery = this.currentTimeDelivered
    )

}

fun CartItem.toCartItemUiModel(): CartUiModel.ItemListCheckout{
    return CartUiModel.ItemListCheckout(
        qty = this.qty.toString(),
        price = this.product.price,
        type = this.type,
        ref =  this.product.combinationReference
    )
}


