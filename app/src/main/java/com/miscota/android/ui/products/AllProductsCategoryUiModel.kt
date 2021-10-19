package com.miscota.android.ui.products

import java.util.*

sealed class AllProductsCategoryUiModel(
    val id: Long = UUID.randomUUID().leastSignificantBits,
    open val categoryName: String,
) {
    data class Header(
        override val categoryName: String,
    ) : AllProductsCategoryUiModel(
        categoryName = categoryName,
    )

    data class SubCategoryUiModel(
        override val categoryName: String,
        val categoryUrl: String,
    ) : AllProductsCategoryUiModel(
        categoryName = categoryName,
    )
}
