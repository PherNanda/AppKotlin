package com.miscota.android.ui.productdetail

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val productDetailModule = module {
    viewModel {
        ProductDetailViewModel(
                authStore = get(),
                productRepository = get(),
                checkoutRepository = get(),
                eventsManager = get()
        )
    }
}