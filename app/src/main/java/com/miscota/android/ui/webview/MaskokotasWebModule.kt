package com.miscota.android.ui.webview

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val maskokotasWebModule = module {
    viewModel {
        MaskokotasWebViewModel (
            authStore = get(),
        )
    }
}