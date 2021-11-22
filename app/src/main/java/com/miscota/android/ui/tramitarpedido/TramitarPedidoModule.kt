package com.miscota.android.ui.tramitarpedido

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val tramitarPedidoModule = module {
    viewModel {
        TramitarPedidoViewModel(
            authStore = get(),
        )
    }
}