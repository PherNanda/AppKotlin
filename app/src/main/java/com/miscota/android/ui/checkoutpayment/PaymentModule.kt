package com.miscota.android.ui.checkoutpayment

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val paymenttModule = module {
    viewModel {
        PaymentMethodsViewModel(
            authStore = get(),
            checkoutRepository = get()
        )
    }
}