package com.miscota.android.util

import android.content.Context
import android.content.SharedPreferences
import com.miscota.android.api.auth.response.LoginResponse
import com.miscota.android.ui.productdetail.CartProduct
import com.miscota.android.ui.productdetail.OptionUiModel
import com.miscota.android.ui.store.Store
import com.squareup.moshi.*
import java.lang.Error
import java.lang.reflect.Type
import java.util.*

interface AuthStore {

    fun getAccessToken(): String?

    fun isLoggedIn(): Boolean

    fun getUser(): UserModel?

    fun getAutoLoginParam(): String?

    fun setAutoLoginParam(autoLoginParam: String?)

    fun getAutoLoginParamExpire(): String?

    fun setAutoLoginParamExpire(autoLoginParamExpire: String?)

    fun setUser(user: UserModel?)

    fun getCard(): CardN?

    fun setCard(card: CardN)

    fun getFcmToken(): String?

    fun setFcmToken(fcm: String?)

    fun getBearerToken(): String?

    fun setBearerToken(bearer: String?)

    fun getAddress(): Address?

    fun getAddressInfo(): Address?

    fun setAddress(address: Address?)

    fun setAddressInfo(addressInfo: Address?)

    fun getRecentAddresses(): List<Address>?

    fun getAddressesUser(): List<Address>?

    fun addAddressUser(address: Address?)

    fun getRecentAddressesInfo(): List<Address>?

    fun addRecentAddress(address: Address?)

    fun addRecentAddressInfo(addressInfo: Address?)

    fun getDeliveredType(): String?

    fun setDeliveredType(deliveredType: String)

    fun getcurrentTimeDelivered(): String?

    fun setCurrentTimeDelivered(currentTimeDelivered: String)

    fun getType(): String?

    fun getCarriers(): String?

    fun setCarriers(cost: String)

    fun getCarriersSd(): String?

    fun setCarriersSd(costSd: String)

    fun getCarriersEco(): String?

    fun setCarriersEco(costEco: String)

    fun setType(type: String)

    fun getEmail(): String?

    fun setEmail(email: String)

    fun getPhone(): String?

    fun setPhone(phone: String)

    fun addToCart(
            qty: Int,
            product: CartProduct,
            selectedCombination: OptionUiModel.Option,
            productType: String,
            stock: Int
    )

    fun removeItemFromCart(
        id: Int,
    )

    fun removeItemFromCartRef(
        ref: String,
        type: String
    )

    fun setQuantityCartItem(
        id: Int,
        quantity: Int
    )

    fun setQuantityCartItemRef(
        id: Int,
        quantity: Int,
        ref: String,
        stock: Int,
        type: String
    )

    fun setTotalCartItens(totalItens: Int)

    fun getTotalCartItens(): Int

    fun getCart(): List<CartItem>

    fun getRetailID():String?

    fun setRetailID(retailID: String?)
}

class DefaultAuthStore constructor(context: Context) : AuthStore {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        SharedPreferencesStore.NAME_PREFERENCES, Context.MODE_PRIVATE
    )

    private val moshi = Moshi.Builder().build()

    override fun getUser(): UserModel? {
        val userJsonAdapter: JsonAdapter<UserModel> = moshi.adapter(UserModel::class.java)
        val userJson = sharedPreferences.getString(KEY_USER_MODEL, null) ?: return null

        return userJsonAdapter.fromJson(userJson)
    }

    override fun getAutoLoginParam(): String? {
        return sharedPreferences.getString(KEY_AUTOLOGIN_PARAM, null)
    }

    override fun setAutoLoginParam(autoLoginParam: String?) {
        with(sharedPreferences.edit()) {
            putString(KEY_AUTOLOGIN_PARAM, autoLoginParam)
            apply()
        }
    }

    override fun getAutoLoginParamExpire(): String? {
        return sharedPreferences.getString(KEY_AUTOLOGIN_PARAM_EXPIRE, null)
    }

    override fun setAutoLoginParamExpire(autoLoginParamExpire: String?) {
        with(sharedPreferences.edit()) {
            putString(KEY_AUTOLOGIN_PARAM_EXPIRE, autoLoginParamExpire)
            apply()
        }
    }

    override fun setUser(user: UserModel?) {
        with(sharedPreferences.edit()) {
            val jsonAdapter: JsonAdapter<UserModel> =
                moshi.adapter(UserModel::class.java)
            val userJson = jsonAdapter.toJson(user)

            putString(KEY_USER_MODEL, userJson)
            if (user == null) {
                putBoolean(KEY_BOOLEAN_LOGGED_IN, false)
            } else {
                putBoolean(KEY_BOOLEAN_LOGGED_IN, true)
            }
            apply()
        }
    }

    override fun getCard(): CardN? {
        val cardJsonAdapter: JsonAdapter<CardN> = moshi.adapter(CardN::class.java)
        val cardJson = sharedPreferences.getString(KEY_CARD_MODEL, null) ?: return null

        return cardJsonAdapter.fromJson(cardJson)
    }

    override fun setCard(card: CardN){
        with(sharedPreferences.edit()) {
            val jsonAdapter: JsonAdapter<CardN> =
                moshi.adapter(CardN::class.java)
            val cardJson = jsonAdapter.toJson(card)

            putString(KEY_CARD_MODEL, cardJson)
            if (card == null) {
                putBoolean(KEY_CARD, false)
            } else {
                putBoolean(KEY_CARD, true)
            }
            apply()
        }
    }

    override fun getAccessToken(): String? {
        return sharedPreferences.getString(KEY_STRING_USER_ACCESS_TOKEN, null)
    }

    override fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_BOOLEAN_LOGGED_IN, false)
    }

    override fun getFcmToken(): String? {
        return sharedPreferences.getString(KEY_STRING_FCM_TOKEN, null)
    }

    override fun setFcmToken(fcm: String?) {
        with(sharedPreferences.edit()) {
            putString(KEY_STRING_FCM_TOKEN, fcm)
            apply()
        }
    }

    override fun getBearerToken(): String? {
        return "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZF91c2VyIjoiMSIsImRhdGUiOiIyMDIxLTAyLTIyIDAwOjA3OjI1In0.Vx085eNL1bLpr5eaYC1zAfaDrgkdXjGKC0SFzaHPxfo"
        //return sharedPreferences.getString(KEY_STRING_BEARER_TOKEN, null)
    }

    override fun setBearerToken(bearer: String?) {
        with(sharedPreferences.edit()) {
            putString(KEY_STRING_BEARER_TOKEN, bearer)
            apply()
        }
    }

    override fun getAddress(): Address? {
        val json = sharedPreferences.getString(KEY_ADDRESS_MODEL, null) ?: return null
        val jsonAdapter: JsonAdapter<Address> =
            moshi.adapter(Address::class.java)
        return try {
            jsonAdapter.fromJson(json)
        } catch (e: Exception) {
            null
        }
    }

    override fun getAddressInfo(): Address? {
        val json = sharedPreferences.getString(KEY_ADDRESS_INFO_MODEL, null) ?: return null
        val jsonAdapter: JsonAdapter<Address> =
            moshi.adapter(Address::class.java)
        return try {
            jsonAdapter.fromJson(json)
        } catch (e: Exception) {
            null
        }
    }

    override fun setAddress(address: Address?) {
            with(sharedPreferences.edit()) {
                val jsonAdapter: JsonAdapter<Address> = moshi.adapter(Address::class.java)
                val json = jsonAdapter.toJson(address)
                putString(KEY_ADDRESS_MODEL, json)
                apply()
            }
    }

    override fun setAddressInfo(addressInfo: Address?) {
            with(sharedPreferences.edit()) {
                val jsonAdapter: JsonAdapter<Address> = moshi.adapter(Address::class.java)


                val json = jsonAdapter.toJson(addressInfo)
                putString(KEY_ADDRESS_INFO_MODEL, json)
                apply()
            }
    }


    override fun getRecentAddresses(): List<Address>? {
        val json = sharedPreferences.getString(KEY_LIST_RECENT_ADDRESS_MODEL, null) ?: return null

        val parameterizeDataType: Type = Types.newParameterizedType(
            List::class.java,
            Address::class.java
        )
        val jsonAdapter: JsonAdapter<List<Address>> = moshi.adapter(parameterizeDataType)
        return try {
            jsonAdapter.fromJson(json)
        } catch (e: Exception) {
            null
        }
    }

    override fun getAddressesUser(): List<Address>?{
        val json = sharedPreferences.getString(KEY_LIST_ADDRESS_USER_MODEL, null) ?: return null

        val parameterizeDataType: Type = Types.newParameterizedType(
            List::class.java,
            Address::class.java
        )
        val jsonAdapter: JsonAdapter<List<Address>> = moshi.adapter(parameterizeDataType)
        return try {
            jsonAdapter.fromJson(json)
        } catch (e: Exception) {
            null
        }
    }

    override fun addAddressUser(address: Address?){
        with(sharedPreferences.edit()) {
            val parameterizeDataType: Type = Types.newParameterizedType(
                List::class.java,
                Address::class.java
            )
            val jsonAdapter: JsonAdapter<List<Address>> = moshi.adapter(parameterizeDataType)

            val recentAddresses = getRecentAddresses()?.toMutableList() ?: mutableListOf()
            if (address != null) {
                recentAddresses.add(0, address)
            }

            val json = jsonAdapter.toJson(recentAddresses.take(3))

            putString(KEY_LIST_ADDRESS_USER_MODEL, json)
            apply()
        }

    }

    override fun getRecentAddressesInfo(): List<Address>?{
        val json = sharedPreferences.getString(KEY_LIST_RECENT_ADDRESS_INFO_MODEL, null) ?: return null

        val parameterizeDataType: Type = Types.newParameterizedType(
            List::class.java,
            Address::class.java
        )
        val jsonAdapter: JsonAdapter<List<Address>> = moshi.adapter(parameterizeDataType)
        return try {
            jsonAdapter.fromJson(json)
        } catch (e: Exception) {
            null
        }
    }

    override fun addRecentAddress(address: Address?) {
        with(sharedPreferences.edit()) {
            val parameterizeDataType: Type = Types.newParameterizedType(
                List::class.java,
                Address::class.java
            )
            val jsonAdapter: JsonAdapter<List<Address>> = moshi.adapter(parameterizeDataType)

            val recentAddresses = getRecentAddresses()?.toMutableList() ?: mutableListOf()
            if (address != null) {
                recentAddresses.add(0, address)
            }

            val json = jsonAdapter.toJson(recentAddresses.take(3))

            putString(KEY_LIST_RECENT_ADDRESS_MODEL, json)
            apply()
        }
    }

    override fun addRecentAddressInfo(addressInfo: Address?) {
        with(sharedPreferences.edit()) {
            val parameterizeDataType: Type = Types.newParameterizedType(
                List::class.java,
                Address::class.java
            )
            val jsonAdapter: JsonAdapter<List<Address>> = moshi.adapter(parameterizeDataType)

            val recentAddresses = getRecentAddressesInfo()?.toMutableList() ?: mutableListOf()
            if (addressInfo != null) {

                /**recentAddresses.map { if (addressInfo != it)
                    recentAddresses.add(0, addressInfo)
                }**/
                recentAddresses.add(0, addressInfo)
            }

            val json = jsonAdapter.toJson(recentAddresses.take(3))

            putString(KEY_LIST_RECENT_ADDRESS_INFO_MODEL, json)
            apply()
        }
    }


    override fun addToCart(
            qty: Int,
            product: CartProduct,
            selectedCombination: OptionUiModel.Option,
            productType: String,
            stock: Int
    ) {
        with(sharedPreferences.edit()) {
            val parameterizeDataType: Type = Types.newParameterizedType(
                List::class.java,
                CartItem::class.java
            )
            val jsonAdapter: JsonAdapter<List<CartItem>> = moshi.adapter(parameterizeDataType)
            var deliveredType = ""
            var currentTime = ""
            val cart = getCart().toMutableList()
            val json = if (cart.any { it.productId == product.productId && it.combinationReference == product.combinationReference && it.type == product.typeProduct})
            {
                println(" it.productId cart[0] ${cart[0]}")
                val newList = cart.map { item ->
                    if (item.productId == product.productId && item.combinationReference == product.combinationReference && item.type == product.typeProduct) {
                        return@map item.copy(qty = item.qty + qty)
                    }
                    deliveredType = item.deliveredTypeOne
                    currentTime = item.currentTimeDelivered.toString()

                    return@map item.copy(
                            //deliveredTypeOne = deliveredType,
                            currentTimeDelivered = currentTime,
                            stock = product.combinationStock
                            //type = productType
                    )
                }
                jsonAdapter.toJson(newList)
            } else {
                cart.add(
                    CartItem(
                        qty = qty,
                        productId = product.productId,
                        product = product,
                        combinationReference = selectedCombination.id,
                        combinationPrice = selectedCombination.optionPrice,
                        deliveredTypeOne = deliveredType,
                        currentTimeDelivered = currentTime,
                        stock = product.combinationStock,
                        type = productType
                    )
                )

                jsonAdapter.toJson(cart)
            }

            putString(KEY_CART_LIST, json)
            apply()
        }
    }

    override fun removeItemFromCart(id: Int) {
        with(sharedPreferences.edit()) {
            val parameterizeDataType: Type = Types.newParameterizedType(
                List::class.java,
                CartItem::class.java
            )
            val jsonAdapter: JsonAdapter<List<CartItem>> = moshi.adapter(parameterizeDataType)

            val cart = getCart().toMutableList()
            val newList = cart.filter { it.productId != id }
            val json = jsonAdapter.toJson(newList)

            putString(KEY_CART_LIST, json)
            apply()
        }
    }

    override fun removeItemFromCartRef(ref: String, type: String) {
        with(sharedPreferences.edit()) {
            val parameterizeDataType: Type = Types.newParameterizedType(
                List::class.java,
                CartItem::class.java
            )
            val jsonAdapter: JsonAdapter<List<CartItem>> = moshi.adapter(parameterizeDataType)
            println(" it.combinationReference != ref && it.type != type     $ref  $type")
            val cart = getCart().toMutableList()
            val newList = cart.filter { it.combinationReference != ref }
            val json = jsonAdapter.toJson(newList)

            putString(KEY_CART_LIST, json)
            apply()
        }
    }

    override fun setQuantityCartItem(id: Int, quantity: Int) {
        with(sharedPreferences.edit()) {
            val parameterizeDataType: Type = Types.newParameterizedType(
                List::class.java,
                CartItem::class.java
            )
            val jsonAdapter: JsonAdapter<List<CartItem>> = moshi.adapter(parameterizeDataType)

            val cart = getCart().toMutableList()
            val newList = cart.map { item ->
                if (item.productId == id) {
                    return@map item.copy(qty = quantity)
                }
                return@map item
            }
            val json = jsonAdapter.toJson(newList)

            putString(KEY_CART_LIST, json)
            apply()
        }
    }

    override fun setQuantityCartItemRef(id: Int, quantity: Int, ref: String, stock: Int, type: String) {
        with(sharedPreferences.edit()) {
            val parameterizeDataType: Type = Types.newParameterizedType(
                List::class.java,
                CartItem::class.java
            )
            val jsonAdapter: JsonAdapter<List<CartItem>> = moshi.adapter(parameterizeDataType)

            val cart = getCart().toMutableList()
            val newList = cart.map { item ->
                if (item.productId == id  && item.combinationReference == ref && item.type == type) {
                    return@map item.copy(qty = quantity)
                }
                return@map item
            }
            val json = jsonAdapter.toJson(newList)

            putString(KEY_CART_LIST, json)
            apply()
        }
    }

    override fun setTotalCartItens(totalItens: Int){
        with(sharedPreferences.edit()) {
            putString(KEY_TOTAL_CART_ITENS, totalItens.toString())
            apply()
        }

    }

    override fun getTotalCartItens(): Int {
        var totalItens = sharedPreferences.getString(KEY_TOTAL_CART_ITENS, null)
        return totalItens?.toInt()?:0
    }

    override fun getCart(): List<CartItem> {
        val json = sharedPreferences.getString(KEY_CART_LIST, null) ?: return listOf()

        val parameterizeDataType: Type = Types.newParameterizedType(
            List::class.java,
            CartItem::class.java
        )
        val jsonAdapter: JsonAdapter<List<CartItem>> = moshi.adapter(parameterizeDataType)

        return try {
            jsonAdapter.fromJson(json) ?: listOf()
        } catch (e: Exception) {
            listOf()
        }
    }

    override fun setDeliveredType(deliveredType: String) {
        with(sharedPreferences.edit()) {
            val parameterizeDataType: Type = Types.newParameterizedType(
                List::class.java,
                CartItem::class.java
            )
            val jsonAdapter: JsonAdapter<List<CartItem>> = moshi.adapter(parameterizeDataType)

            val cart = getCart().toMutableList()
            val newList = cart.map { item ->
                if (item.product.typeProduct == "sameday") {
                    return@map item.copy(deliveredTypeOne = deliveredType)
                }

                return@map item
            }
            val json = jsonAdapter.toJson(newList)

            putString(KEY_CART_LIST, json)
            putString(KEY_DELIVERED_TYPE_MODEL, json)
            apply()
        }
    }

    override fun getDeliveredType(): String? {
        val json = sharedPreferences.getString(KEY_DELIVERED_TYPE_MODEL, null) ?: return null

        val parameterizeDataType: Type = Types.newParameterizedType(
            List::class.java,
            CartItem::class.java,
        )
        val jsonAdapter: JsonAdapter<String> = moshi.adapter(parameterizeDataType)
        return try {
            jsonAdapter.fromJson(json)
        } catch (e: Exception) {
            null
        }
    }

    override fun setCurrentTimeDelivered(currentTimeDelivered: String) {
       with(sharedPreferences.edit()) {
            val parameterizeDataType: Type = Types.newParameterizedType(
                List::class.java,
                CartItem::class.java
            )
            val jsonAdapter: JsonAdapter<List<CartItem>> = moshi.adapter(parameterizeDataType)

            val cart = getCart().toMutableList()
            val newList = cart.map { item ->
                if (item.product.typeProduct == "sameday") {
                    return@map item.copy(currentTimeDelivered = currentTimeDelivered)
                }
                return@map item
            }
            val json = jsonAdapter.toJson(newList)

            putString(KEY_CART_LIST, json)
            putString(KEY_CURRENT_TIME_DELIVERED, json)
            apply()
        }



    }



    override fun getcurrentTimeDelivered(): String? {
        val json = sharedPreferences.getString(KEY_CURRENT_TIME_DELIVERED, null) ?: return null

        val parameterizeDataType: Type = Types.newParameterizedType(
            List::class.java,
            CartItem::class.java,
        )
        val jsonAdapter: JsonAdapter<String> = moshi.adapter(parameterizeDataType)
        return try {
            jsonAdapter.fromJson(json)
        } catch (e: Exception) {
            null
        }
    }

    override fun setType(type: String) {
        with(sharedPreferences.edit()) {
            putString(KEY_TYPE, type)
            apply()
        }
    }

    override fun getType(): String? {
        return sharedPreferences.getString(KEY_TYPE, null)
    }

    override fun getCarriers(): String? {
        return sharedPreferences.getString(KEY_CALL_CARRIERS, null)
    }

    override fun setCarriers(costAllDeliver: String) {
        with(sharedPreferences.edit()) {
            putString(KEY_CALL_CARRIERS, costAllDeliver)
            apply()
        }
    }

    override fun getCarriersSd(): String? {
        return sharedPreferences.getString(KEY_SD_CARRIERS, null)
    }

    override fun setCarriersSd(costSd: String) {
        with(sharedPreferences.edit()) {
            putString(KEY_SD_CARRIERS, costSd)
            apply()
        }

    }
    override fun getCarriersEco(): String? {
        return sharedPreferences.getString(KEY_ECO_CARRIERS, null)
    }

    override fun setCarriersEco(costEco: String) {
        with(sharedPreferences.edit()) {
            putString(KEY_ECO_CARRIERS, costEco)
            apply()
        }

    }

    override fun setEmail(email: String) {
        with(sharedPreferences.edit()) {
            putString(KEY_EMAIL, email)
            apply()
        }
    }

    override fun getEmail(): String? {
        return sharedPreferences.getString(KEY_EMAIL, null)
    }

    override fun setPhone(phone: String) {
        with(sharedPreferences.edit()) {
            putString(KEY_PHONE, phone)
            apply()
        }
    }

    override fun getPhone(): String? {
        return sharedPreferences.getString(KEY_PHONE, null)
    }

    override fun getRetailID():String? {
        return sharedPreferences.getString(KEY_LIST_STORE_RETAIL, null)
    }

    override fun setRetailID(retailID: String?) {
        with(sharedPreferences.edit()) {
            putString(KEY_LIST_STORE_RETAIL, retailID)
            apply()
        }
    }

    companion object {
        private const val KEY_USER_MODEL = "user"
        private const val KEY_BOOLEAN_LOGGED_IN = "loggedIn"
        private const val KEY_STRING_USER_ACCESS_TOKEN = "token"
        private const val KEY_STRING_BEARER_TOKEN = "bearerToken"
        private const val KEY_STRING_FCM_TOKEN = "fcmToken"
        private const val KEY_ADDRESS_MODEL = "address"
        private const val KEY_LIST_RECENT_ADDRESS_MODEL = "recent_address"
        private const val KEY_CART_LIST = "cart_list"
        private const val KEY_AUTOLOGIN_PARAM = "autologin_param"
        private const val KEY_DELIVERED_TYPE_MODEL = "delivered_type"
        private const val KEY_CURRENT_TIME_DELIVERED ="currentTimeDelivered"
        private const val KEY_TYPE ="type"
        private const val KEY_EMAIL ="email"
        private const val KEY_PHONE ="phone"
        private const val KEY_ADDRESS_INFO_MODEL = "address_info"
        private const val KEY_LIST_RECENT_ADDRESS_INFO_MODEL = "recent_address_info"
        private const val KEY_CALL_CARRIERS = "total_carriers"
        private const val KEY_SD_CARRIERS = "sd_carriers"
        private const val KEY_ECO_CARRIERS = "eco_carriers"
        private const val KEY_TOTAL_CART_ITENS = "total_cart_itens"
        private const val KEY_AUTOLOGIN_PARAM_EXPIRE = "autologin_param_expire"
        private const val KEY_LIST_STORE_RETAIL = "list_store_retails"
        private const val KEY_LIST_ADDRESS_USER_MODEL = "user_addresses"
        private const val KEY_CARD_MODEL = "card_user"
        private const val KEY_CARD = "card_in"
    }
}

@JsonClass(generateAdapter = true)
data class Address(
    @Json(name = "address") val address: String,
    @Json(name = "address_number") val addressNumber: String,
    @Json(name = "lat") val lat: Double,
    @Json(name = "lng") val lng: Double,
    @Json(name = "state") val state: String, //region
    @Json(name = "postalCode") val postalCode: String,
    @Json(name = "city") val city: String,
    @Json(name = "region") val region: String?,
    @Json(name = "phone") val phone: String?,
    @Json(name = "country_id_country") val countryId: String?,
    @Json(name = "country_name") val countryName: String?,
    @Json(name = "country_code") val countryCode: String?,
    @Json(name = "country_ctry_lang") val countrylang: String?,
)

@JsonClass(generateAdapter = true)
data class UserModel(
    @Json(name = "id_user") val userId: Long,
    @Json(name = "name") val name: String?,
    @Json(name = "email") val email: String,
    @Json(name = "sex") val sex: Int?,
    @Json(name = "birthdate") val birthdate: String?,
    @Json(name = "phone") val phone: String?,
    @Json(name = "surname") val surname: String?

)

@JsonClass(generateAdapter = true)
data class CartItem(
    @Json(name = "qty") val qty: Int,
    @Json(name = "productId") val productId: Int,
    @Json(name = "product") val product: CartProduct,
    @Json(name = "selectedCombination") val combinationReference: String,
    @Json(name = "combinationPrice") val combinationPrice: Double,
    @Json(name = "deliveredType") val deliveredTypeOne: String,
    @Json(name = "currentTimeDelivered") val currentTimeDelivered: String?,
    @Json(name = "stock") val stock: Int?,
    @Json(name = "type") val type: String?
)

@JsonClass(generateAdapter = true)
data class Item(
    @Json(name = "uid") val uyd: Long,
    @Json(name = "productId") val productId: Int,
    @Json(name = "productName") val productName: Int,
    @Json(name = "productPrice") val productPrice: String,
    @Json(name = "oldPrice") val oldPrice: String,
    @Json(name = "image") val image: Double,
    @Json(name = "quantity") val quantity: String,
    @Json(name = "discount") val discount: String,
    @Json(name = "stock") val stock: Int?
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
    @Json(name = "country_ctry_lang") val countrylang: String?,
)

@JsonClass(generateAdapter = true)
data class CardN(
    @Json(name = "card") val card: String,
    @Json(name = "security") val security: String,
    @Json(name = "expireYear") val expireYear: String,
    @Json(name = "expireMonth") val expireMonth: String,
    @Json(name = "owner") val owner: String?,
)

@JsonClass(generateAdapter = true)
data class ErrorLogin(
    @Json(name = "error") val error: String
    )

fun LoginResponse.Info.toUserModel(): UserModel {
    return UserModel(userId, name, email, sex, birthdate, phone, surname)
}

fun LoginResponse.Error.toErrorModel(): ErrorLogin {
    return ErrorLogin(error)
}

fun LoginResponse.AddressInfo.toAddress(): AddressInfo {
    return AddressInfo(idAddress, title, name, surnames, idUser, idCountry, region,
        city, address, phone, phone2, postcode, default, obs, countryId, countryName, countryCode, countrylang)
}