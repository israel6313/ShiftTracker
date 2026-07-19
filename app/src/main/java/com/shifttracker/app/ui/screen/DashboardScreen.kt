package com.shifttracker.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shifttracker.app.ui.components.ShiftCard
import com.shifttracker.app.ui.theme.*
import com.shifttracker.app.ui.viewmodel.DashboardViewModel
import com.shifttracker.app.utils.DateTimeUtils
import com.shifttracker.app.utils.WageCalculator

@Composable
fun DashboardScreen(
    userName: String,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 32.dp)
    ) {
        // Greeting header
        item {
            Column {
                Text(
                    text = state.greeting,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Text(
                    text = userName.ifBlank { "ישראל" },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = OnBackground
                )
            }
        }

        // Main stats card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Surface),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Month selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.nextMonth() }) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = "הבא", tint = OnSurface)
                        }
                        Text(
                            text = "${DateTimeUtils.getHebrewMonthName(state.selectedMonth)} ${state.selectedYear}",
                            style = MaterialTheme.typography.titleMedium,
                            color = OnSurface
                        )
                        IconButton(onClick = { viewModel.previousMonth() }) {
                            Icon(Icons.Default.ChevronRight, contentDescription = "הקודם", tint = OnSurface)
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Total earnings
                    Text(
                        text = "₪ ${ "%.2f".format(state.totalEarnings) }",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Primary
                    )
                    Text(
                        text = "סך הכנסות חודשיות",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Spacer(Modifier.height(20.dp))
                    Divider(color = DividerColor)
                    Spacer(Modifier.height(16.dp))

                    // Stats row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = DateTimeUtils.formatHours(state.totalHours),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                            Text(text = "שעות עבודה", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                        VerticalDivider(modifier = Modifier.height(40.dp), color = DividerColor)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${state.totalShifts}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                            Text(text = "משמרות", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                    }
                }
            }
        }

        // Recent shifts header
        if (state.recentShifts.isNotEmpty()) {
            item {
                Text(
                    text = "משמרות אחרונות",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = OnBackground,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            items(state.recentShifts) { shiftWithJob ->
                ShiftCard(shiftWithJob = shiftWithJob, onDelete = {})
            }
        }
    }
}
