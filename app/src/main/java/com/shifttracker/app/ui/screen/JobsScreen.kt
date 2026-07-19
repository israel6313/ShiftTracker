package com.shifttracker.app.ui.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shifttracker.app.data.model.Job
import com.shifttracker.app.ui.components.AddEditJobSheet
import com.shifttracker.app.ui.theme.*
import com.shifttracker.app.ui.viewmodel.JobsViewModel
import com.shifttracker.app.utils.DateTimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobsScreen(viewModel: JobsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    var showAddSheet by remember { mutableStateOf(false) }
    var jobToEdit by remember { mutableStateOf<Job?>(null) }
    var showDeleteDialog by remember { mutableStateOf<Job?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "מקומות עבודה",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = OnBackground
            )
            IconButton(
                onClick = { jobToEdit = null; showAddSheet = true },
                colors = IconButtonDefaults.iconButtonColors(containerColor = Primary)
            ) {
                Icon(Icons.Default.Add, contentDescription = "הוסף עבודה", tint = OnPrimary)
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.jobStats, key = { it.job.id }) { stats ->
                val jobColor = try { Color(android.graphics.Color.parseColor(stats.job.colorHex)) } catch (e: Exception) { Primary }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(width = 2.dp, color = jobColor, shape = RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: delete/edit actions
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(onClick = { jobToEdit = stats.job; showAddSheet = true }) {
                                Icon(Icons.Default.Edit, "ערוך", tint = TextSecondary)
                            }
                            IconButton(onClick = { showDeleteDialog = stats.job }) {
                                Icon(Icons.Default.Delete, "מחק", tint = ErrorColor)
                            }
                        }

                        Spacer(Modifier.weight(1f))

                        // Right: job info
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = stats.job.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                            Text(
                                text = "₪ ${ "%.2f".format(stats.job.hourlyWage) } / שעה",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                            Spacer(Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(DateTimeUtils.formatHours(stats.totalHours), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = Primary)
                                    Text("סך שעות", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("₪ ${ "%.0f".format(stats.totalEarnings) }", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = SuccessColor)
                                    Text("סך שכר", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete confirmation
    showDeleteDialog?.let { job ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("מחיקת עבודה", color = OnSurface) },
            text = { Text("האם למחוק את \"${job.name}\"? ההיסטוריה של המשמרות תישמר.", color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteJob(job); showDeleteDialog = null }) {
                    Text("מחק", color = ErrorColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) { Text("ביטול", color = Primary) }
            },
            containerColor = Surface
        )
    }

    if (showAddSheet) {
        AddEditJobSheet(
            job = jobToEdit,
            onSave = { viewModel.saveJob(it); showAddSheet = false },
            onDismiss = { showAddSheet = false }
        )
    }
}
