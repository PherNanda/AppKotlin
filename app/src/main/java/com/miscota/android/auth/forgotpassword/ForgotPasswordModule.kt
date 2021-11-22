package com.miscota.android.auth.forgotpassword

import com.miscota.android.auth.forgotpassword.ui.ForgotPasswordViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val forgotPasswordModule = module {
    viewModel {
        ForgotPasswordViewModel(
            authRepository = get(),
            authStore = get(),
        )
    }
}