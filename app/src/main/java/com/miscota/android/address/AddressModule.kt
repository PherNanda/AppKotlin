package com.miscota.android.address

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val addressModule = module {
    viewModel {
        AddressViewModel(
            authStore = get(),
            placesRepository = get(),
        )
    }
}