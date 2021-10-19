package com.miscota.android.ui.category


data class CategoryItemTagItem(
    val categories: ArrayList<Category>,
    val id: String,
    val name: String
)


data class Category(
    val categories: ArrayList<String>,
    val id: String,
    val name: String
)

