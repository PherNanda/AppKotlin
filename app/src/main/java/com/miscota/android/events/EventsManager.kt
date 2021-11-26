package com.miscota.android.events

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.miscota.android.ui.cart.CartUiModel
import com.miscota.android.ui.productdetail.CartProduct
import com.miscota.android.ui.productdetail.Product
import com.miscota.android.util.CartItem

interface EventsManager{

    fun viewItem(productDetail: Product, brand: String): Bundle
    fun viewItemEvent(productDetail: Product, screenClass: String, methodName: String)
    fun indexItem(item: Bundle): Bundle
    fun viewItemList(viewItemList: Product, itemWithIndex: Bundle)
    fun selectItem(itemJogging: Bundle)
    fun itemToCart(productCart: CartProduct, brand: String): Bundle
    fun addToCart(myItemToCart: Bundle, cartProduct: CartProduct, qty: Int)
    fun itemRemoveToCart(cartItem: CartUiModel.Item,): Bundle
    fun removeFromCart(myItemToCart: Bundle, cartItem: CartUiModel.Item, qty: Int)
    fun itemCheckout(item: CartUiModel.ItemListCheckout): Bundle
    fun beginCheckout(myItemToCart: Bundle, total: Double)
    fun viewItemCart(cartItem: CartItem): Bundle
    fun viewCart(cartItem: CartItem, viewItemCart: Bundle)
    fun shippingInfo(itemCheckout: CartUiModel.ItemListCheckout, order: String, item: Bundle)
    fun purchase(cartItem: Bundle, order: String, item: Bundle, cartShip: Double, total: Double, eventsInfo: EventsInfo)

}
class DefaultEventsManager constructor(context: Context) : EventsManager {

    private val CURRENCY_DEFAULT = "EUR"
    var index = 0L
    val analytics = Firebase.analytics

    override fun viewItem(productDetail: Product, brand: String): Bundle {

        val price = productDetail.price.substring(0, 4)
        return Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_ID, productDetail.productId.toString())
                putString(FirebaseAnalytics.Param.ITEM_NAME, productDetail.productTitle)
                putString(FirebaseAnalytics.Param.ITEM_CATEGORY, productDetail.productType)
                putString(FirebaseAnalytics.Param.ITEM_VARIANT, productDetail.productDescription)
                putString(FirebaseAnalytics.Param.ITEM_BRAND, productDetail.brand)
                putDouble(
                    FirebaseAnalytics.Param.PRICE,
                    price.replace(",", ".").toDouble()
                )
                putString(FirebaseAnalytics.Param.CURRENCY, CURRENCY_DEFAULT)
            }
    }

    override fun viewItemEvent(productDetail: Product, screenClass: String, methodName: String) {
        analytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM) {
        param(FirebaseAnalytics.Param.SCREEN_NAME, "view_item")
        param(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
        param(FirebaseAnalytics.Param.METHOD, methodName)
        param(FirebaseAnalytics.Param.CURRENCY, CURRENCY_DEFAULT)
        param(FirebaseAnalytics.Param.ITEM_NAME, productDetail.productTitle)
        param(FirebaseAnalytics.Param.ITEM_BRAND, productDetail.brand)
        }
    }

    override fun indexItem(item: Bundle): Bundle {
        return Bundle(item).apply {
            putLong(FirebaseAnalytics.Param.INDEX, index + 1)
        }
    }

    override fun viewItemList(viewItemList: Product, itemWithIndex: Bundle) {
        analytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST) {
            param(
                FirebaseAnalytics.Param.ITEM_LIST_ID,
                "Product_ID_" + viewItemList.productId
            )
            param(FirebaseAnalytics.Param.ITEM_LIST_NAME, viewItemList.productTitle)
            param(
                FirebaseAnalytics.Param.ITEMS,
                arrayOf(itemWithIndex)
            )
        }
    }

    override fun selectItem(itemJogging: Bundle) {
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(FirebaseAnalytics.Param.ITEM_LIST_ID, "List ID ${index++}")
            param(FirebaseAnalytics.Param.ITEM_LIST_NAME, "Related products")
            param(FirebaseAnalytics.Param.ITEMS, arrayOf(itemJogging))
        }
    }

    //productDetailViewModel line 153
    override fun itemToCart(productCart: CartProduct, brand: String): Bundle {
        return Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_ID, productCart.productId.toString())
                putString(FirebaseAnalytics.Param.ITEM_NAME, productCart.title)
                putString(FirebaseAnalytics.Param.ITEM_CATEGORY, productCart.typeProduct)
                putString(FirebaseAnalytics.Param.ITEM_VARIANT, productCart.combinationReference)
                putString(FirebaseAnalytics.Param.ITEM_BRAND, productCart.title)
                putDouble(
                    FirebaseAnalytics.Param.PRICE,
                    productCart.combinationPrice
                )
                putString(FirebaseAnalytics.Param.CURRENCY, CURRENCY_DEFAULT)
            }
    }
    //productDetailViewModel line 154
    override fun addToCart(myItemToCart: Bundle, cartProduct: CartProduct, qty: Int) {
        analytics.logEvent(FirebaseAnalytics.Event.ADD_TO_CART) {
            param(FirebaseAnalytics.Param.CURRENCY, CURRENCY_DEFAULT)
            param(FirebaseAnalytics.Param.VALUE, qty * cartProduct.combinationPrice)
            param(FirebaseAnalytics.Param.ITEM_NAME, cartProduct.title)
            param(FirebaseAnalytics.Param.ITEM_CATEGORY, cartProduct.typeProduct)
            param(FirebaseAnalytics.Param.ITEM_VARIANT, cartProduct.combinationReference)
            param(FirebaseAnalytics.Param.QUANTITY, qty.toString())
            param(FirebaseAnalytics.Param.PRICE, cartProduct.combinationPrice)
            param(FirebaseAnalytics.Param.ITEMS, arrayOf(myItemToCart))
        }
    }
    //CartActivity line 644
    override fun itemRemoveToCart(cartItem: CartUiModel.Item): Bundle {
        return Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_ID, cartItem.productId.toString())
                putString(FirebaseAnalytics.Param.ITEM_NAME, cartItem.productName)
                putString(FirebaseAnalytics.Param.ITEM_CATEGORY, cartItem.type)
                putString(FirebaseAnalytics.Param.ITEM_VARIANT, cartItem.reference)
                putString(FirebaseAnalytics.Param.ITEM_BRAND, cartItem.brand)
                putDouble(
                    FirebaseAnalytics.Param.PRICE,
                    cartItem.price
                )
                putString(FirebaseAnalytics.Param.CURRENCY, CURRENCY_DEFAULT)
            }
    }
    //CartActivity line 645
    override fun removeFromCart(myItemToCart: Bundle, cartItem: CartUiModel.Item, qty: Int) {

        analytics.logEvent(FirebaseAnalytics.Event.REMOVE_FROM_CART) {
            param(FirebaseAnalytics.Param.CURRENCY, CURRENCY_DEFAULT)
            param(FirebaseAnalytics.Param.VALUE, qty * cartItem.price)
            param(FirebaseAnalytics.Param.PRICE, cartItem.price)
            param(FirebaseAnalytics.Param.ITEM_NAME, cartItem.productName)
            param(FirebaseAnalytics.Param.ITEMS, arrayOf(myItemToCart))
        }
    }

    override fun itemCheckout(item: CartUiModel.ItemListCheckout): Bundle {

        return Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, item.ref)
            putString(FirebaseAnalytics.Param.ITEM_CATEGORY, item.type)
            putString(FirebaseAnalytics.Param.QUANTITY, item.qty)
            putDouble(FirebaseAnalytics.Param.VALUE, item.qty.toDouble() * item.price.toDouble())
            putDouble(
                FirebaseAnalytics.Param.PRICE,
                item.price.toDouble()
            )
            putString(FirebaseAnalytics.Param.CURRENCY, CURRENCY_DEFAULT)
        }
    }

    override fun beginCheckout(myItemToCart: Bundle, total: Double){
        analytics.logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT)
        {
            param(FirebaseAnalytics.Param.CURRENCY, CURRENCY_DEFAULT)
            param(FirebaseAnalytics.Param.VALUE, total)
            param(FirebaseAnalytics.Param.COUPON, "MISCOTA")
            param(FirebaseAnalytics.Param.ITEMS, arrayOf(myItemToCart))
        }
    }
    //cartActivity line 522 y 523
    override fun viewItemCart(cartItem: CartItem): Bundle {
        return Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_ID, cartItem.productId.toString())
                putString(FirebaseAnalytics.Param.ITEM_NAME, cartItem.product.title)
                putString(FirebaseAnalytics.Param.ITEM_CATEGORY, cartItem.type)
                putString(FirebaseAnalytics.Param.ITEM_VARIANT, cartItem.combinationReference)
                putString(FirebaseAnalytics.Param.ITEM_BRAND, cartItem.product.title)
                putDouble(FirebaseAnalytics.Param.PRICE, cartItem.product.combinationPrice)
                putString(FirebaseAnalytics.Param.ITEM_CATEGORY, cartItem.product.typeProduct)
            }
    }
    //cartActivity line 522 y 523
    override fun viewCart(cartItem: CartItem, viewItemCart: Bundle){
        analytics.logEvent(FirebaseAnalytics.Event.VIEW_CART) {
            param(FirebaseAnalytics.Param.CURRENCY, CURRENCY_DEFAULT)
            param(FirebaseAnalytics.Param.VALUE, cartItem.qty * cartItem.product.combinationPrice)
            param(FirebaseAnalytics.Param.PRICE, cartItem.product.combinationPrice)
            param(FirebaseAnalytics.Param.ITEM_CATEGORY, cartItem.type!!)
            param(FirebaseAnalytics.Param.ITEM_ID, cartItem.combinationReference)
            param(FirebaseAnalytics.Param.ITEM_NAME, cartItem.product.title)
            param(FirebaseAnalytics.Param.ITEMS, arrayOf(viewItemCart))
        }

    }

    override fun shippingInfo(itemCheckout: CartUiModel.ItemListCheckout, order: String, item: Bundle){
        analytics.logEvent(FirebaseAnalytics.Event.ADD_SHIPPING_INFO) {
            param(FirebaseAnalytics.Param.CURRENCY, CURRENCY_DEFAULT)
            param(FirebaseAnalytics.Param.VALUE, itemCheckout.qty.toDouble() * itemCheckout.price.toDouble())
            param(FirebaseAnalytics.Param.COUPON, "CUPON_DESCUENTO")
            param(FirebaseAnalytics.Param.SHIPPING_TIER, itemCheckout.type!!)
            param(FirebaseAnalytics.Param.SHIPPING, order)
            param(FirebaseAnalytics.Param.ITEMS, arrayOf(item))
        }
    }
    override fun purchase(cartItem: Bundle, order: String, item: Bundle, cartShip: Double, total: Double, eventsInfo: EventsInfo) {
        analytics.logEvent(FirebaseAnalytics.Event.PURCHASE) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, eventsInfo.screenName)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, eventsInfo.screenClass)
            param(FirebaseAnalytics.Param.METHOD,  eventsInfo.methodName)
            param(FirebaseAnalytics.Param.TRANSACTION_ID, order)
            param(FirebaseAnalytics.Param.AFFILIATION, "Miscota_App_Android")
            param(FirebaseAnalytics.Param.CURRENCY, CURRENCY_DEFAULT)
            param(FirebaseAnalytics.Param.VALUE, total)
            param(FirebaseAnalytics.Param.TAX, 0.0)
            param(FirebaseAnalytics.Param.SHIPPING, cartShip)
            param(FirebaseAnalytics.Param.COUPON, "WITHOUT_COUPON_")
            param(FirebaseAnalytics.Param.ITEMS, arrayOf(cartItem))
        }
        // [END log_purchase]
    }

}

data class EventsInfo(
    val screenName: String,
    val methodName: String,
    val screenClass: String
)
