package com.miscota.android.ui

import java.util.UUID

abstract class UiModel(
    open val uid: Long = UUID.randomUUID().mostSignificantBits
) {
    override fun hashCode(): Int {
        return uid.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UiModel

        if (uid != other.uid) return false

        return true
    }
}
