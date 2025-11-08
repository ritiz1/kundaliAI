package com.example.kundaliai

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kundaliai.astrologyAPIHandle.BirthData
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserQueryForm(
    onSubmit: (BirthData) -> Unit
) {
    // Form state
    var name by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var place by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf(0.0) }
    var longitude by remember { mutableStateOf(0.0) }

    // Time dropdown state
    var selectedHour12 by remember { mutableStateOf<Int?>(null) }
    var selectedMinute by remember { mutableStateOf<Int?>(null) }
    var isPm by remember { mutableStateOf<Boolean?>(null) }

    var hourExpanded by remember { mutableStateOf(false) }
    var minuteExpanded by remember { mutableStateOf(false) }
    var periodExpanded by remember { mutableStateOf(false) }

    val hours = (1..12).toList()
    val minutes = (0..59).toList()
    val periods = listOf("AM", "PM")

    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }

    fun openDatePicker() {
        val y = calendar.get(Calendar.YEAR)
        val m = calendar.get(Calendar.MONTH)
        val d = calendar.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(context, { _, yy, mm, dd ->
            date = String.format(Locale.US, "%02d/%02d/%04d", dd, mm + 1, yy)
        }, y, m, d).show()
    }

    fun to24Hour(h12: Int, pm: Boolean): Int = when {
        pm && h12 in 1..11 -> h12 + 12
        !pm && h12 == 12 -> 0
        else -> h12
    }

    val timeReady = selectedHour12 != null && selectedMinute != null && isPm != null

    // Shared dark field colors
    val fieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color(0xFF18141A),
        unfocusedContainerColor = Color(0xFF18141A),
        disabledContainerColor = Color(0xFF18141A),
        focusedIndicatorColor = Color(0xFFE53E3E),
        unfocusedIndicatorColor = Color(0xFF6A4F55),
        disabledIndicatorColor = Color(0xFF6A4F55),
        focusedLabelColor = Color(0xFFE0E0E0),
        unfocusedLabelColor = Color(0xFFE0E0E0),
        disabledLabelColor = Color(0xFFA8A8A8),
        cursorColor = Color(0xFFE53E3E),
        focusedPlaceholderColor = Color(0xFF7A7075),
        unfocusedPlaceholderColor = Color(0xFF7A7075)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2C0F17), Color(0xFF12080C))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo placeholder (no icon to avoid dependency issues)
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFFE42222), Color(0xFF680C0C))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Optional: small inner circle to mimic a logo mark
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f))
                )
            }

            Spacer(Modifier.height(24.dp))
            Text(
                text = "Enter Your Birth Details",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Provide accurate information for personalized Jyotish insights",
                color = Color(0xFFB9B0B3),
                fontSize = 14.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(28.dp))

            // Name
            Text("Full Name", color = Color.White, fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter your name") },
                singleLine = true,
                colors = fieldColors
            )
            Spacer(Modifier.height(20.dp))

            // Date
            Text("Date of Birth", color = Color.White, fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = date,
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { openDatePicker() },
                placeholder = { Text("Pick a date") },
                enabled = false,
                colors = fieldColors,
                singleLine = true
            )
            Spacer(Modifier.height(20.dp))

            // Time of Birth
            Text("Time of Birth", color = Color.White, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Hour dropdown
                ExposedDropdownMenuBox(
                    expanded = hourExpanded,
                    onExpandedChange = { hourExpanded = !hourExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedHour12?.toString() ?: "",
                        onValueChange = {},
                        modifier = Modifier.menuAnchor(),
                        placeholder = { Text("Hour") },
                        readOnly = true,
                        colors = fieldColors,
                        singleLine = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(hourExpanded) }
                    )
                    ExposedDropdownMenu(expanded = hourExpanded, onDismissRequest = { hourExpanded = false }) {
                        hours.forEach { h ->
                            DropdownMenuItem(text = { Text(h.toString()) }, onClick = {
                                selectedHour12 = h
                                hourExpanded = false
                            })
                        }
                    }
                }
                // Minute dropdown
                ExposedDropdownMenuBox(
                    expanded = minuteExpanded,
                    onExpandedChange = { minuteExpanded = !minuteExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedMinute?.let { String.format(Locale.US, "%02d", it) } ?: "",
                        onValueChange = {},
                        modifier = Modifier.menuAnchor(),
                        placeholder = { Text("Min") },
                        readOnly = true,
                        colors = fieldColors,
                        singleLine = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(minuteExpanded) }
                    )
                    ExposedDropdownMenu(expanded = minuteExpanded, onDismissRequest = { minuteExpanded = false }) {
                        minutes.forEach { m ->
                            DropdownMenuItem(text = { Text(String.format(Locale.US, "%02d", m)) }, onClick = {
                                selectedMinute = m
                                minuteExpanded = false
                            })
                        }
                    }
                }
                // AM/PM dropdown
                ExposedDropdownMenuBox(
                    expanded = periodExpanded,
                    onExpandedChange = { periodExpanded = !periodExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = isPm?.let { if (it) "PM" else "AM" } ?: "",
                        onValueChange = {},
                        modifier = Modifier.menuAnchor(),
                        placeholder = { Text("AM/PM",fontSize= 10.sp) },
                        readOnly = true,
                        colors = fieldColors,
                        singleLine = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(periodExpanded) }
                    )
                    ExposedDropdownMenu(expanded = periodExpanded, onDismissRequest = { periodExpanded = false }) {
                        periods.forEach { p ->
                            DropdownMenuItem(text = { Text(p) }, onClick = {
                                isPm = p == "PM"
                                periodExpanded = false
                            })
                        }
                    }
                }
            }
            Spacer(Modifier.height(20.dp))

            // Place
            Text("Place of Birth", color = Color.White, fontWeight = FontWeight.Medium)
            PlaceAutocompleteField(
                onPlaceSelected = { selectedPlace, lat, lon ->
                    place = selectedPlace
                    latitude = lat
                    longitude = lon
                },
                modifier = Modifier.fillMaxWidth(),
                fieldColors = fieldColors
            )
            Spacer(Modifier.height(32.dp))

            // Button
            val canSubmit = name.isNotEmpty() && date.isNotEmpty() && place.isNotEmpty() && timeReady
            Button(
                onClick = {
                    val hour24 = to24Hour(selectedHour12!!, isPm!!)
                    val data = BirthData(
                        name = name,
                        date = date,
                        hours = hour24,
                        minutes = selectedMinute!!,
                        seconds = 0,
                        place = place,
                        latitude = latitude,
                        longitude = longitude
                    )
                    onSubmit(data)
                },
                enabled = canSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE42222),
                    disabledContainerColor = Color(0xFF4A2E32)
                )
            ) {
                Text("Generate Kundali", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Your data is secure and used only for astrological calculations",
                color = Color(0xFF8C7E82),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
