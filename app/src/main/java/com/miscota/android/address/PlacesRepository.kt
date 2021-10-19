package com.miscota.android.address

import android.content.Context
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import kotlinx.coroutines.tasks.await

class PlacesRepository(context: Context) {

    private val placesClient = Places.createClient(context)

    private val token = AutocompleteSessionToken.newInstance()

    suspend fun getPlaceSuggestions(query: String): List<PlaceSuggestion> {
        val request = FindAutocompletePredictionsRequest.builder()
            .setSessionToken(token)
            .setQuery(query)
            .build()

        val response = placesClient.findAutocompletePredictions(request).await()

        return response.autocompletePredictions.map {
            PlaceSuggestion(it.placeId, it.getFullText(null).toString())
        }
    }

    suspend fun getPlaceById(placeId: String): PlaceDetails {
        val placeFields = listOf(
            Place.Field.ID,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG,
            Place.Field.ADDRESS_COMPONENTS
        )

        val request = FetchPlaceRequest.newInstance(placeId, placeFields)
        val response = placesClient.fetchPlace(request).await()

        val place = response.place

        val address = place.address?.split(",")?.firstOrNull() ?: ""
        val latlng = place.latLng
        var state = ""
        var city = ""
        var zipCode = ""

        place.addressComponents?.asList()?.forEach { item ->
            when {
                item.types.contains("postal_code") -> {
                    zipCode = item.name
                }
                item.types.contains("administrative_area_level_1") -> {
                    state = item.name
                }
                item.types.contains("locality") -> {
                    city = item.name
                }
            }
        }
        return PlaceDetails(
            placeId = place.id ?: "",
            address = address ?: "",
            state = state,
            city = city,
            zipCode = zipCode,
            latitude = latlng?.latitude ?: 0.0,
            longitude = latlng?.longitude ?: 0.0
        )
    }
}

data class PlaceSuggestion(
    val placeId: String,
    val address: String
)

data class PlaceDetails(
    val placeId: String,
    val address: String,
    val city: String,
    val state: String,
    val zipCode: String,
    val longitude: Double,
    val latitude: Double
)
