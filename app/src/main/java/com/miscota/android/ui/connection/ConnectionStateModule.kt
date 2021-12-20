package com.miscota.android.ui.connection

import com.miscota.android.api.ApiProvider
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val connectionStateModule = module {
    viewModel {
        ConnectionStateViewModel(
            authStore = get(),
            authApi = get<ApiProvider>().authApi,
        )
    }
}