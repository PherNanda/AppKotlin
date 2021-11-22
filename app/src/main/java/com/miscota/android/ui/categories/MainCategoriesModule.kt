package com.miscota.android.ui.categories

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainCategoriesModule = module {
    viewModel {
        MainCategoriesViewModel(
            authStore = get(),
            categoryRepository = get(),
        )
    }

    viewModel {
        SubCategoriesViewModel()
    }
}