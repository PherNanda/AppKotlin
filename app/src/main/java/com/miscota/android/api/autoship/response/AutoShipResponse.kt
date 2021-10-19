package com.miscota.android.api.autoship.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AutoShipResponse(
    @Json(name = "id_autoshipping") val autoShippingId: Long,
    @Json(name = "frequency") val frequency: Long,
    @Json(name = "next_deliver_date") val nextDeliveryDate: String?,
    @Json(name = "items") val items: List<Item>,
) {
    @JsonClass(generateAdapter = true)
    data class Item(
        @Json(name = "id_autoshipping") val autoShippingId: Long,
        @Json(name = "id_product") val productId: Long,
        @Json(name = "ref") val ref: Long,
        @Json(name = "quantity") val quantity: Int,
        @Json(name = "provider") val provider: Int,
        @Json(name = "shipping_time_order") val shippingTimeOrder: Int,
        @Json(name = "cost_price") val costPrice: Double,
        @Json(name = "stock") val stock: Int,
        @Json(name = "bloqued") val bloqued: Int,
        @Json(name = "pick_cost_price") val pickCostPrice: Double,
        @Json(name = "units_pack") val unitsPack: Int,
        @Json(name = "weight") val weight: Double,
        @Json(name = "price") val price: Double,
        @Json(name = "tax_perc") val taxPercentage: Int,
        @Json(name = "width") val width: Double?,
        @Json(name = "height") val height: Double?,
        @Json(name = "length") val length: Double?,
        @Json(name = "cat_h") val cat_h: Double,
        @Json(name = "cat_w") val cat_w: Double,
        @Json(name = "cat_l") val cat_l: Double,
        @Json(name = "algorithm") val algorithm: String,
        @Json(name = "limits") val limits: String,
        @Json(name = "extra") val extra: String,
        @Json(name = "competitor") val competitor: String?,
        @Json(name = "type") val type: String?,
        @Json(name = "bloqued_all") val bloquedAll: Int,
        @Json(name = "status") val status: Int,
        @Json(name = "title") val title: String,
        @Json(name = "title_en") val titleEn: String?,
        @Json(name = "title_es") val titleEs: String?,
        @Json(name = "title_ext") val titleExt: String,
        @Json(name = "url_thumb") val urlThumb: String,
        @Json(name = "name_brand") val nameBrand: String,
        @Json(name = "id_campaign") val campaignId: Int?,
        @Json(name = "camp_price") val campPrice: Boolean?,
        @Json(name = "priceu") val priceu: Double,
        @Json(name = "disc_mp") val discMp: Double,
        @Json(name = "thumb") val thumb: String,
    )
}
