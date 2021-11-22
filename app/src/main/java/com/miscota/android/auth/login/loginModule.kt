package com.miscota.android.auth.login

import com.miscota.android.auth.login.ui.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val loginModule = module {
    viewModel {
        LoginViewModel(
            authRepository = get(),
            authStore = get(),
        )
    }
}