package com.miscota.android.ui.home

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {
    viewModel {
        HomeViewModel(
            authRepository = get(),
            authStore = get(),
            autoShipRespository = get(),
            storeLocationRepository = get()
        )
    }
}