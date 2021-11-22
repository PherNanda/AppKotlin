package com.miscota.android.ui.categories

import android.net.Uri
import android.os.Parcelable
import com.miscota.android.R
import com.miscota.android.models.CategoryNetworkModel
import com.miscota.android.ui.UiModel
import kotlinx.parcelize.Parcelize
import java.util.*

sealed class MainCategoriesUiModel : UiModel() {

    object LoaderItem : MainCategoriesUiModel()

    @Parcelize
    data class Item(
        override val uid: Long = UUID.randomUUID().leastSignificantBits,
        val title: String,
        val imageUrl: String?,
        val id: Long,
        val subItems: List<Item>,
    ) : MainCategoriesUiModel(), Parcelable
}

fun CategoryNetworkModel.toUiModel() = MainCategoriesUiModel.Item(
    title = this.name,
    id = this.id,
    imageUrl = Uri.parse("android.resource://com.miscota.android/" + getImageFromCategory(this)).toString(),
    subItems = categories.map { item ->
        MainCategoriesUiModel.Item(
            title = item.name,
            id = item.id,
            imageUrl = null,
            subItems = item.categories.map { subItem ->
                MainCategoriesUiModel.Item(
                    title = subItem.name,
                    id = subItem.id,
                    imageUrl = "",
                    subItems = listOf(),
                )
            },
        )
    },
)

fun getImageFromCategory(item: CategoryNetworkModel): Int {
    return when (item.name) {
        "Perros" ->  R.drawable.ic_dog_category
        "Gatos" -> R.drawable.ic_cat_category
        "Roedores" -> R.drawable.ic_rabbit_category
        "Pájaros" -> R.drawable.ic_bird_category
        "Peces" -> R.drawable.ic_fish_category
        "Reptiles" -> R.drawable.ic_reptile_category
        "Caballos" -> R.drawable.ic_horse_category
        else -> return 0
    }
}

