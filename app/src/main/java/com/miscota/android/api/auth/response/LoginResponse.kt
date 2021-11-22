package com.miscota.android.api.auth.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.lang.Error
import java.util.*

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "autologin_param") val autologin_param: String,
    @Json(name = "autologin_param_expire") val autologin_param_expire: String,
    @Json(name = "info") val info: Info,
    @Json(name = "addresses") val address: List<Address>?,
    @Json(name = "addresse") val addressInfo: List<com.miscota.android.util.AddressInfo>?,
    @Json(name = "error") val error: Error?
) {
    @JsonClass(generateAdapter = true)
    data class Info(
        @Json(name = "id_user") val userId: Long,
        @Json(name = "name") val name: String,
        @Json(name = "email") val email: String,
        @Json(name = "sex") val sex: Int?,
        @Json(name = "birthdate") val birthdate: String?,
        @Json(name = "phone") val phone: String?,
        @Json(name = "surname") val surname: String?
    )

   @JsonClass(generateAdapter = true)
    data class Error(
        @Json(name = "error") val error: String
    )

    @JsonClass(generateAdapter = true)
    data class Address(
        @Json(name = "id_address") val idAddress: String,
        @Json(name = "title") val title: String?,
        @Json(name = "name") val name: String?,
        @Json(name = "surnames") val surnames: String?,
        @Json(name = "id_user") val idUser: String?,
        @Json(name = "id_country") val idCountry: String?,
        @Json(name = "region") val region: String?,
        @Json(name = "city") val city: String?,
        @Json(name = "address") val address: String?,
        @Json(name = "phone") val phone: String?,
        @Json(name = "phone2") val phone2: String?,
        @Json(name = "postcode") val postcode: String?,
        @Json(name = "default") val default: String?,
        @Json(name = "obs") val obs: String?,
        @Json(name = "country_id_country") val countryId: String?,
        @Json(name = "country_name") val countryName: String?,
        @Json(name = "country_code") val countryCode: String?,
        @Json(name = "country_ctry_lang") val countrylang: String?,
    )


    @JsonClass(generateAdapter = true)
    data class AddressInfo(
        @Json(name = "id_address") val idAddress: String,
        @Json(name = "title") val title: String?,
        @Json(name = "name") val name: String?,
        @Json(name = "surnames") val surnames: String?,
        @Json(name = "id_user") val idUser: String?,
        @Json(name = "id_country") val idCountry: String?,
        @Json(name = "region") val region: String?,
        @Json(name = "city") val city: String?,
        @Json(name = "address") val address: String?,
        @Json(name = "phone") val phone: String?,
        @Json(name = "phone2") val phone2: String?,
        @Json(name = "postcode") val postcode: String?,
        @Json(name = "default") val default: String?,
        @Json(name = "obs") val obs: String?,
        @Json(name = "country_id_country") val countryId: String?,
        @Json(name = "country_name") val countryName: String?,
        @Json(name = "country_code") val countryCode: String?,
        @Json(name = "country_ctry_lang") val countrylang: String?
    )
}
