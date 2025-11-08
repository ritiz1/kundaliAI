package com.example.kundaliai

import android.content.Context
import android.location.Geocoder
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceAutocompleteField(
    onPlaceSelected: (place: String, lat: Double, lon: Double) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    fieldColors: TextFieldColors = TextFieldDefaults.colors()
) {
    var query by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf<List<String>>(emptyList()) }
    var expanded by remember { mutableStateOf(false) }
    var isValid by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(modifier = modifier) {
        OutlinedTextField(
            value = query,
            onValueChange = { newQuery ->
                query = newQuery
                isValid = false
                if (newQuery.length >= 3) {
                    expanded = true
                    scope.launch {
                        suggestions = getPlaceSuggestions(context, newQuery)
                    }
                } else {
                    suggestions = emptyList()
                    expanded = false
                }
            },
            label = { Text("Enter birth place") },
            isError = !isValid && query.isNotEmpty(),
            supportingText = {
                if (!isValid && query.isNotEmpty()) {
                    Text("Please select from dropdown", color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = fieldColors,
            singleLine = true
        )

        DropdownMenu(
            expanded = expanded && suggestions.isNotEmpty(),
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            suggestions.forEach { suggestion ->
                DropdownMenuItem(
                    text = { Text(suggestion) },
                    onClick = {
                        query = suggestion
                        expanded = false
                        isValid = true
                        scope.launch {
                            val coords = getCoordinates(context, suggestion)
                            coords?.let { (lat, lon) ->
                                onPlaceSelected(suggestion, lat, lon)
                            }
                        }
                    }
                )
            }
        }
    }
}

suspend fun getPlaceSuggestions(context: Context, query: String): List<String> {
    return withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context)
            val addresses = geocoder.getFromLocationName(query, 5)
            addresses?.map {
                "${it.locality ?: it.subAdminArea ?: it.adminArea}, ${it.countryName}"
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}

suspend fun getCoordinates(context: Context, place: String): Pair<Double, Double>? {
    return withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context)
            val addresses = geocoder.getFromLocationName(place, 1)
            addresses?.firstOrNull()?.let {
                Pair(it.latitude, it.longitude)
            }
        } catch (e: Exception) {
            null
        }
    }
}
