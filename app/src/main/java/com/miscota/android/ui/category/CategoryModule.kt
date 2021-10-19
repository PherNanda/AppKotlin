package com.miscota.android.ui.category

import android.os.Bundle
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val categoryModule = module {
    viewModel { (bundle: Bundle) ->
        val categoryId = requireNotNull(
            //bundle.getIntegerArrayList("listCategory")
             bundle.getStringArrayList("listCategory")
        )

            CategoryViewModel(
                categoryId = categoryId[0].split("}").firstOrNull()?.toLong()?:0L,
                productRepository = get(),
                autoStore = get()
            )

    }
}