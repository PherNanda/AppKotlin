package com.miscota.android.ui.category

import android.os.Parcel
import android.os.Parcelable


data class CategoryItemTagItem(
    val categories: ArrayList<Category>,
    val id: String,
    val name: String
)


data class Category(
    val categories: ArrayList<CategoryOne>,
    val id: String,
    val name: String
)

data class CategoryOne(
    val category: String?,
    val id: String?,
    val name: String?,
    val checked: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(category)
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(checked)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CategoryOne> {
        override fun createFromParcel(parcel: Parcel): CategoryOne {
            return CategoryOne(parcel)
        }

        override fun newArray(size: Int): Array<CategoryOne?> {
            return arrayOfNulls(size)
        }
    }
}

