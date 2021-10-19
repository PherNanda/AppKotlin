package com.miscota.android.splash.ui

import com.miscota.android.api.ApiProvider
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val splashModule = module {
    viewModel {
        SplashViewModel(
            authStore = get(),
            authApi = get<ApiProvider>().authApi,
        )
    }
}