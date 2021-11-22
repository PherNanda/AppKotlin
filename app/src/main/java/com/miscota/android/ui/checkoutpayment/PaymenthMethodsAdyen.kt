package com.miscota.android.ui.checkoutpayment

data class PaymenthMethodsAdyen(
    val amount: Amount,
    val merchantAccount: String = "MiscotaCOM",
    val reference: String
)


