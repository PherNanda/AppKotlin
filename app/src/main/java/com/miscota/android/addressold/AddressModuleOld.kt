package com.miscota.android.addressold

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val addressModuleold = module {
    viewModel {
        AddressViewModelOld(
            authStore = get(),
            placesRepository = get(),
        )
    }
}