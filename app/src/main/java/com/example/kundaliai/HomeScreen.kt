// File: `app/src/main/java/com/example/kundaliai/HomeScreen.kt`
package com.example.kundaliai

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.kundaliai.navigation.LiveSessionDestination
import com.example.kundaliai.roomRepository.birthDetails.User
import com.example.kundaliai.ui.viewmodels.HomeViewModel
import com.example.kundaliai.ui.theme.*
import androidx.compose.ui.graphics.GraphicsLayerScope
import com.example.kundaliai.navigation.UserQueryDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val savedPeople by viewModel.savedPeople.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGradient)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Kundali AI") }
                )
            },
            floatingActionButton = {
                GradientPlusFab {
                    navController.navigate(UserQueryDestination.route)
                }
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome to Kundali AI",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(32.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Saved People (${savedPeople.size})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    IconButton(onClick = { viewModel.refreshUsers() }) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator() }
                    }
                    savedPeople.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No saved people yet.\nCreate your first kundali!",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    else -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(savedPeople) { user ->
                                SavedPersonCard(
                                    user = user,
                                    onClick = {
                                        navController.navigate("${LiveSessionDestination.route}/${user.userId}")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GradientPlusFab(onClick: () -> Unit) {
    // Custom gold gradient FAB with plus icon
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color.Transparent,
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
        border = BorderStroke(1.dp, BrightGold.copy(alpha = 0.9f))
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(AccentGoldGradient, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "New",
                tint = DeepSlate,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun SavedPersonCard(
    user: User,
    onClick: () -> Unit,
    relation: String? = null,
    rashi: String? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = DarkSlate.copy(alpha = 0.5f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Maroon, DarkMaroon)
                            )
                        )
                        .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.name.firstOrNull()?.toString() ?: "",
                        color = RichGold,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    )
                }

                Column {
                    Text(
                        text = user.name,
                        color = OffWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        relation?.takeIf { it.isNotBlank() }?.let {
                            Surface(
                                color = DeepSlate.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(4.dp),
                                border = BorderStroke(1.dp, RichGold.copy(alpha = 0.2f))
                            ) {
                                Text(
                                    text = it,
                                    color = RichGold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }

                        rashi?.takeIf { it.isNotBlank() }?.let {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(text = "🌙", fontSize = 10.sp)
                                Text(
                                    text = it,
                                    color = MediumGray,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        MetadataItem(
                            icon = Icons.Rounded.CalendarMonth,
                            text = "${user.birthDate} • ${user.birthTime}"
                        )
                        MetadataItem(
                            icon = Icons.Rounded.LocationOn,
                            text = user.place
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(32.dp)
                    .background(Color.White.copy(alpha = 0.05f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = "View Details",
                    tint = DarkGray,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun MetadataItem(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = DarkAmber,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = text,
            color = DarkGray,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
