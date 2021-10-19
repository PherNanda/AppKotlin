package com.miscota.android.address

import com.miscota.android.auth.authModule
import com.miscota.android.util.Address
import com.miscota.android.util.AddressInfo
import java.util.UUID

/**
 * Model class for AutoComplete place suggestions.
 */
data class PlaceSuggestionUiModel(
    val id: Long = UUID.randomUUID().mostSignificantBits,
    val placeId: String,
    val addressText: String,
    val address: Address
) {
    override fun toString(): String {
        // Return address to show in dropdown list for ArrayAdapter
        return addressText
    }
}
