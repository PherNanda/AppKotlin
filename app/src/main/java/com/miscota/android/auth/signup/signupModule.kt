package com.miscota.android.auth.signup

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val signupModule = module {
    viewModel {
        SignupViewModel(
            authRepository = get(),
            authStore = get(),
        )
    }
}