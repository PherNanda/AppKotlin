package com.miscota.android.ui.products

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val homeProductsModule = module {
    viewModel {
        HomeProductsViewModel(
                storeLocationRepository = get(),
                authStore = get()
        )
    }

    viewModel {
        AllProductsCategoryViewModel()
    }
}