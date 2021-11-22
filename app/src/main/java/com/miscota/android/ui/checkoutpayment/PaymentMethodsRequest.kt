package com.miscota.android.ui.checkoutpayment


data class PaymentMethodsRequest(
    val merchantAccount: String,
    val shopperReference: String,
    val amount: Amount,
    val countryCode: String = "ES",
    val shopperLocale: String = "es_ES",
    val telephoneNumber: String = "606123456",
    val shopperEmail: String = "info@miscota.es",
    val paymentMethod: PaymentMethod
    //val shopperName: ShopperName = ShopperName(),
    //val billingAddress: Address = Address(),
    //val deliveryAddress: Address = Address()
)
