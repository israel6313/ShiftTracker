package com.shifttracker.app.ui.screen

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shifttracker.app.data.model.Shift
import com.shifttracker.app.ui.components.ShiftCard
import com.shifttracker.app.ui.theme.*
import com.shifttracker.app.ui.viewmodel.ShiftsViewModel
import com.shifttracker.app.utils.DateTimeUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShiftsScreen(viewModel: ShiftsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var jobDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filter header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Month navigation
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { viewModel.nextMonth() }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.ChevronLeft, "הבא", tint = TextSecondary)
                        }
                        Text(
                            "${DateTimeUtils.getHebrewMonthName(state.selectedMonth)} ${state.selectedYear}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurface
                        )
                        IconButton(onClick = { viewModel.previousMonth() }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.ChevronRight, "הקודם", tint = TextSecondary)
                        }
                    }

                    // Job filter dropdown
                    Box {
                        TextButton(
                            onClick = { jobDropdownExpanded = true },
                            colors = ButtonDefaults.textButtonColors(contentColor = Primary)
                        ) {
                            val selectedJobName = state.jobs.find { it.id == state.selectedJobId }?.name ?: "כל העבודות"
                            Text(selectedJobName, style = MaterialTheme.typography.bodySmall)
                            Icon(Icons.Default.ArrowDropDown, null)
                        }
                        DropdownMenu(
                            expanded = jobDropdownExpanded,
                            onDismissRequest = { jobDropdownExpanded = false },
                            modifier = Modifier.background(Surface)
                        ) {
                            DropdownMenuItem(
                                text = { Text("כל העבודות", color = OnSurface) },
                                onClick = { viewModel.filterByJob(null); jobDropdownExpanded = false }
                            )
                            state.jobs.forEach { job ->
                                DropdownMenuItem(
                                    text = { Text(job.name, color = OnSurface) },
                                    onClick = { viewModel.filterByJob(job.id); jobDropdownExpanded = false }
                                )
                            }
                        }
                    }
                }
            }

            if (state.shifts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("אין משמרות בתקופה זו", color = TextSecondary)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.shifts, key = { it.shift.id }) { shiftWithJob ->
                        ShiftCard(
                            shiftWithJob = shiftWithJob,
                            onDelete = { deleted ->
                                viewModel.deleteShift(deleted)
                                scope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "המשמרת נמחקה",
                                        actionLabel = "ביטול",
                                        duration = SnackbarDuration.Short
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        viewModel.restoreShift(deleted)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
