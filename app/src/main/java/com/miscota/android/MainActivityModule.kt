package com.miscota.android

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainActivityModule = module {
    viewModel {
        MainActivityViewModel(
            authStore = get(),
            checkoutRepository = get(),
            storeLocationRepository = get()
        )
    }
}