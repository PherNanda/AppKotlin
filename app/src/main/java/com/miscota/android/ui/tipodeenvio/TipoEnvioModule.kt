package com.miscota.android.ui.tipodeenvio


import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val tipoEnvioModule = module {
    viewModel {
        TipoEnvioViewModel(
            authStore = get(),
            checkoutRepository = get(),
            eventsManager = get(),
        )
    }
}