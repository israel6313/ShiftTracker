package com.shifttracker.app.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shifttracker.app.ui.theme.*
import com.shifttracker.app.ui.viewmodel.AddShiftViewModel
import com.shifttracker.app.utils.DateTimeUtils
import kotlinx.coroutines.launch
import java.time.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddShiftSheet(
    onDismiss: () -> Unit,
    viewModel: AddShiftViewModel = hiltViewModel()
) {
    val jobs by viewModel.jobs.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedJobId by remember { mutableStateOf(0L) }
    var jobDropdownExpanded by remember { mutableStateOf(false) }
    var startDateTime by remember { mutableStateOf(LocalDateTime.now().minusHours(8)) }
    var endDateTime by remember { mutableStateOf(LocalDateTime.now()) }
    var breakMinutes by remember { mutableStateOf("") }
    var travelExpenses by remember { mutableStateOf("") }
    var bonusAmount by remember { mutableStateOf("") }
    var usedVehicle by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun showDateTimePicker(current: LocalDateTime, onPicked: (LocalDateTime) -> Unit) {
        DatePickerDialog(context, { _, y, m, d ->
            TimePickerDialog(context, { _, h, min ->
                onPicked(LocalDateTime.of(y, m + 1, d, h, min))
            }, current.hour, current.minute, true).show()
        }, current.year, current.monthValue - 1, current.dayOfMonth).show()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                "הוספת משמרת ידנית",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = OnSurface
            )

            // Job selector
            Box {
                OutlinedButton(
                    onClick = { jobDropdownExpanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = if (selectedJobId == 0L) TextSecondary else OnSurface),
                    border = ButtonDefaults.outlinedButtonBorder.copy()
                ) {
                    Text(jobs.find { it.id == selectedJobId }?.name ?: "בחר מקום עבודה")
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Default.ArrowDropDown, null)
                }
                DropdownMenu(
                    expanded = jobDropdownExpanded,
                    onDismissRequest = { jobDropdownExpanded = false },
                    modifier = androidx.compose.ui.Modifier.fillMaxWidth()
                ) {
                    jobs.forEach { job ->
                        DropdownMenuItem(
                            text = { Text(job.name, color = OnSurface) },
                            onClick = { selectedJobId = job.id; jobDropdownExpanded = false }
                        )
                    }
                }
            }

            // Start time
            OutlinedButton(
                onClick = { showDateTimePicker(startDateTime) { startDateTime = it } },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = OnSurface)
            ) {
                Icon(Icons.Default.AccessTime, null, tint = Primary, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("כניסה: ${startDateTime.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yy HH:mm"))}")
            }

            // End time
            OutlinedButton(
                onClick = { showDateTimePicker(endDateTime) { endDateTime = it } },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = OnSurface)
            ) {
                Icon(Icons.Default.ExitToApp, null, tint = Primary, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("יציאה: ${endDateTime.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yy HH:mm"))}")
            }

            // Break, expenses, bonus
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = breakMinutes,
                    onValueChange = { breakMinutes = it },
                    label = { Text("הפסקה (דקות)", color = TextSecondary) },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = DividerColor, focusedTextColor = OnSurface, unfocusedTextColor = OnSurface),
                    singleLine = true
                )
                OutlinedTextField(
                    value = travelExpenses,
                    onValueChange = { travelExpenses = it },
                    label = { Text("נסיעות (₪)", color = TextSecondary) },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = DividerColor, focusedTextColor = OnSurface, unfocusedTextColor = OnSurface),
                    singleLine = true
                )
            }

            OutlinedTextField(
                value = bonusAmount,
                onValueChange = { bonusAmount = it },
                label = { Text("בונוס / טיפ (₪)", color = TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = DividerColor, focusedTextColor = OnSurface, unfocusedTextColor = OnSurface),
                singleLine = true
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = usedVehicle, onCheckedChange = { usedVehicle = it }, colors = CheckboxDefaults.colors(checkedColor = Primary))
                Text("שימוש ברכב", color = OnSurface)
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("הערות (אופציונלי)", color = TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = DividerColor, focusedTextColor = OnSurface, unfocusedTextColor = OnSurface),
                minLines = 2
            )

            errorMessage?.let {
                Text(it, color = ErrorColor, style = MaterialTheme.typography.bodySmall)
            }

            Button(
                onClick = {
                    scope.launch {
                        isSaving = true
                        val success = viewModel.saveShift(
                            jobId = selectedJobId,
                            startMs = DateTimeUtils.localDateTimeToEpoch(startDateTime),
                            endMs = DateTimeUtils.localDateTimeToEpoch(endDateTime),
                            breakMinutes = breakMinutes.toIntOrNull() ?: 0,
                            travelExpenses = travelExpenses.toDoubleOrNull() ?: 0.0,
                            bonusAmount = bonusAmount.toDoubleOrNull() ?: 0.0,
                            usedVehicle = usedVehicle,
                            notes = notes
                        )
                        if (success) onDismiss()
                        else errorMessage = "בדוק שבחרת עבודה וזמן תקין"
                        isSaving = false
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                enabled = !isSaving
            ) {
                if (isSaving) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = OnPrimary)
                else Text("שמור משמרת", fontWeight = FontWeight.Bold, color = OnPrimary)
            }
        }
    }
}
