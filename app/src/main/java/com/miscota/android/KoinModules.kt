package com.miscota.android

import com.miscota.android.address.addressModule
import com.miscota.android.addressold.addressModuleold
import com.miscota.android.auth.authModule
import com.miscota.android.auth.forgotpassword.forgotPasswordModule
import com.miscota.android.auth.login.loginModule
import com.miscota.android.auth.signup.signupModule
import com.miscota.android.splash.ui.splashModule
import com.miscota.android.ui.cart.cartModule
import com.miscota.android.ui.categories.mainCategoriesModule
import com.miscota.android.ui.category.categoryModule
import com.miscota.android.ui.checkoutpayment.paymenttModule
import com.miscota.android.ui.connection.connectionStateModule
import com.miscota.android.ui.home.homeModule
import com.miscota.android.ui.productdetail.productDetailModule
import com.miscota.android.ui.products.homeProductsModule
import com.miscota.android.ui.webview.maskokotasWebModule
import com.miscota.android.ui.scheduledordered.scheduledOrderModule
import com.miscota.android.ui.search.searchModule
import com.miscota.android.ui.store.storeLocationModule
import com.miscota.android.ui.tipodeenvio.tipoEnvioModule
import com.miscota.android.ui.tramitarpedido.tramitarPedidoModule

val koinModules = listOf(
    appModule,
    splashModule,
    loginModule,
    signupModule,
    authModule,
    mainActivityModule,
    forgotPasswordModule,
    homeModule,
    addressModule,
    homeProductsModule,
    mainCategoriesModule,
    categoryModule,
    productDetailModule,
    maskokotasWebModule,
    cartModule,
    scheduledOrderModule,
    searchModule,
    storeLocationModule,
    paymenttModule,
    addressModuleold,
    tramitarPedidoModule,
    tipoEnvioModule,
    connectionStateModule
)
