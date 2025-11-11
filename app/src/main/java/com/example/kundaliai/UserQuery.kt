package com.example.kundaliai

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kundaliai.astrologyAPIHandle.BirthData
import com.example.kundaliai.ui.theme.*
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserQueryForm(
    onSubmit: (BirthData) -> Unit
) {
    // State
    var name by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var place by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf(0.0) }
    var longitude by remember { mutableStateOf(0.0) }

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
        DatePickerDialog(
            context,
            { _, yy, mm, dd -> date = String.format(Locale.US, "%02d/%02d/%04d", dd, mm + 1, yy) },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun to24Hour(h12: Int, pm: Boolean): Int = when {
        pm && h12 in 1..11 -> h12 + 12
        !pm && h12 == 12 -> 0
        else -> h12
    }

    val canSubmit = name.isNotBlank() && date.isNotBlank() && place.isNotBlank() &&
            selectedHour12 != null && selectedMinute != null && isPm != null

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = KundaliDarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .background(KundaliOrangePrimary),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "☸", fontSize = 40.sp, color = KundaliTextWhite)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "|| ॐ ॐ ||",
                color = KundaliOrangePrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Enter Your Birth Details",
                color = KundaliTextWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = "Where Stars Speak Your Truth",
                color = KundaliOrangePrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Provide accurate information for personalized Jyotish insights",
                color = KundaliTextLight,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Name Field
            FormField(
                label = "Full Name",
                icon = Icons.Default.Person
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Enter your name", color = KundaliTextLight.copy(alpha = 0.5f)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = KundaliInputBackground,
                        unfocusedContainerColor = KundaliInputBackground,
                        focusedBorderColor = KundaliOrangePrimary,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = KundaliOrangePrimary,
                        focusedTextColor = KundaliTextWhite,
                        unfocusedTextColor = KundaliTextWhite
                    )
                )
            }

            // Date Field
            FormField(
                label = "Date of Birth",
                icon = Icons.Default.Person
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(KundaliInputBackground)
                        .clickable { openDatePicker() }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = if (date.isEmpty()) "Select date" else date,
                        color = if (date.isEmpty()) KundaliTextLight.copy(alpha = 0.5f) else KundaliTextWhite,
                        fontSize = 16.sp
                    )
                }
            }

            // Time Field
            FormField(
                label = "Time of Birth",
                icon = Icons.Default.Person
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StyledDropdown(
                        value = selectedHour12?.toString() ?: "Hour",
                        expanded = hourExpanded,
                        onExpandedChange = { hourExpanded = it },
                        options = hours.map { it.toString() },
                        onOptionSelected = { selectedHour12 = it.toInt() },
                        modifier = Modifier.weight(1f)
                    )
                    StyledDropdown(
                        value = selectedMinute?.let { String.format(Locale.US, "%02d", it) } ?: "Min",
                        expanded = minuteExpanded,
                        onExpandedChange = { minuteExpanded = it },
                        options = minutes.map { String.format(Locale.US, "%02d", it) },
                        onOptionSelected = { selectedMinute = it.toInt() },
                        modifier = Modifier.weight(1f)
                    )
                    StyledDropdown(
                        value = isPm?.let { if (it) "PM" else "AM" } ?: "AM/PM",
                        expanded = periodExpanded,
                        onExpandedChange = { periodExpanded = it },
                        options = periods,
                        onOptionSelected = { isPm = it == "PM" },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Place Field
            FormField(
                label = "Place of Birth",
                icon = Icons.Default.LocationOn
            ) {
                PlaceAutocompleteField(
                    onPlaceSelected = { selectedPlace, lat, lon ->
                        place = selectedPlace
                        latitude = lat
                        longitude = lon
                    }
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = {
                    val hour24 = to24Hour(selectedHour12!!, isPm!!)
                    onSubmit(
                        BirthData(
                            name = name,
                            date = date,
                            hours = hour24,
                            minutes = selectedMinute!!,
                            seconds = 0,
                            place = place,
                            latitude = latitude,
                            longitude = longitude
                        )
                    )
                },
                enabled = canSubmit,
                colors = ButtonDefaults.buttonColors(containerColor = KundaliOrangePrimary,
                    disabledContainerColor=KundaliInputBackground,
                    disabledContentColor=KundaliTextLight.copy(alpha=0.5f)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(
                    text = "Generate Kundali",
                    color = KundaliTextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "ॐ Shanti Shanti Shanti ॐ",
                color = KundaliTextLight.copy(alpha = 0.8f),
                fontSize = 12.sp
            )
            Text(
                text = "Your sacred data is protected by karmic encryption",
                color = KundaliTextLight.copy(alpha = 0.6f),
                fontSize = 10.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}

@Composable
private fun FormField(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = label, tint = KundaliOrangePrimary)
            Spacer(Modifier.width(8.dp))
            Text(text = label, color = KundaliTextLight, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
        Spacer(Modifier.height(8.dp))
        content()
    }
}

@Composable
private fun StyledDropdown(
    value: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = KundaliOrangePrimary)
            },
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = KundaliInputBackground,
                unfocusedContainerColor = KundaliInputBackground,
                focusedBorderColor = KundaliOrangePrimary,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = KundaliOrangePrimary,
                focusedTextColor = KundaliTextWhite,
                unfocusedTextColor = KundaliTextWhite
            ),
            singleLine = true
        )
        Spacer(modifier = Modifier.matchParentSize().clickable { onExpandedChange(true) })
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier.background(KundaliInputBackground)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = KundaliTextWhite) },
                    onClick = {
                        onOptionSelected(option)
                        onExpandedChange(false)
                    },
                    modifier = Modifier.background(KundaliInputBackground)
                )
            }
        }
    }
}
