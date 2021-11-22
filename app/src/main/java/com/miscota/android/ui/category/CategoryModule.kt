package com.miscota.android.ui.category

import android.os.Bundle
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val categoryModule = module {
    viewModel { (bundle: Bundle) ->
        val categoryId = requireNotNull(
             bundle.getParcelableArrayList("listCategory")?: arrayListOf<CategoryOne>()
        )

            CategoryViewModel(
                categorys = categoryId,
                productRepository = get(),
                autoStore = get()
            )

    }
}