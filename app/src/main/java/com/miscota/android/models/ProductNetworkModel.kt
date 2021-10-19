package com.miscota.android.models


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductNetworkModel(

    @Json(name = "brand") val brand: String,
    @Json(name = "combinations") val combinations: List<Combination>,
    @Json(name = "id_product") val idProduct: Int,
    @Json(name = "new_price_text") val newPriceText: String?,
    @Json(name = "price_text") val priceText: String,
    @Json(name = "pvp_price_text") val pvpPriceText: String?,
    @Json(name = "shop_url") val shopUrl: String?,
    @Json(name = "short_descr") val shortDescr: String?,
    @Json(name = "descr") val descr: String?,
    @Json(name = "thumb_img_raw") val thumbImg: String?,
    @Json(name = "thumb_img_2_raw") val thumbImg2: String?,
    @Json(name = "thumb_img_3_raw") val thumbImg3: String?,
    @Json(name = "thumb_img_4_raw") val thumbImg4: String?,
    @Json(name = "thumb_img_5_raw") val thumbImg5: String?,
    @Json(name = "thumb_img_6_raw") val thumbImg6: String?,
    @Json(name = "thumb_img_7_raw") val thumbImg7: String?,
    @Json(name = "thumb_img_8_raw") val thumbImg8: String?,
    @Json(name = "thumb_img_9_raw") val thumbImg9: String?,
    @Json(name = "thumb_img_10_raw") val thumbImg10: String?,
    @Json(name = "title") val title: String,
    @Json(name = "variations") val variations: String?,
    @Json(name = "productType") val productType: String?
) {
    @JsonClass(generateAdapter = true)
    data class Combination(
        @Json(name = "id_product") val idProduct: Int?,
        @Json(name = "ref") val ref: String?,
        @Json(name = "price") val price: Double?,
        @Json(name = "variation") val variation: String?,
        @Json(name = "units_pack") val unitsPack: Int?,
        @Json(name = "stock") val stock: Int?,
    )
}
