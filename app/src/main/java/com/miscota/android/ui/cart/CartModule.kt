package com.miscota.android.ui.cart

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val cartModule = module {
    viewModel {
        CartViewModel(
            authStore = get(),
            checkoutRepository = get(),
            eventsManager = get()
        )
    }
}