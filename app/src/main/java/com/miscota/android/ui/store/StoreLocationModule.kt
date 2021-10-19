package com.miscota.android.ui.store

import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val storeLocationModule = module {
    viewModel {
        StoreLocationViewModel(
            app = androidApplication(),
            storeLocationRepository = get(),
            authStore = get(),
        )
    }
}