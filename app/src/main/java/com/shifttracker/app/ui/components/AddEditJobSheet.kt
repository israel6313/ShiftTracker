package com.shifttracker.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.shifttracker.app.data.model.Job
import com.shifttracker.app.ui.theme.*

private val JOB_COLORS = listOf(
    "#4A90D9", "#E8536A", "#30D158", "#FF9F0A",
    "#BF5AF2", "#FF6B35", "#64D2FF", "#FFD60A"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditJobSheet(
    job: Job?,
    onSave: (Job) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(job?.name ?: "") }
    var wage by remember { mutableStateOf(job?.hourlyWage?.toString() ?: "") }
    var selectedColor by remember { mutableStateOf(job?.colorHex ?: JOB_COLORS.first()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (job == null) "הוספת מקום עבודה" else "עריכת ${job.name}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = OnSurface
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("שם מקום העבודה", color = TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = DividerColor,
                    focusedTextColor = OnSurface,
                    unfocusedTextColor = OnSurface
                ),
                singleLine = true
            )

            OutlinedTextField(
                value = wage,
                onValueChange = { wage = it },
                label = { Text("שכר בסיס לשעה (₪)", color = TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = DividerColor,
                    focusedTextColor = OnSurface,
                    unfocusedTextColor = OnSurface
                ),
                singleLine = true
            )

            Text("צבע מזהה", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.height(100.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(JOB_COLORS) { hex ->
                    val color = try { Color(android.graphics.Color.parseColor(hex)) } catch (e: Exception) { Primary }
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(color)
                            .then(
                                if (selectedColor == hex)
                                    Modifier.border(3.dp, Color.White, CircleShape)
                                else Modifier
                            )
                            .clickable { selectedColor = hex }
                    )
                }
            }

            Button(
                onClick = {
                    val hourlyWage = wage.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank() && hourlyWage > 0) {
                        onSave(Job(id = job?.id ?: 0L, name = name, hourlyWage = hourlyWage, colorHex = selectedColor))
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("שמור", fontWeight = FontWeight.Bold, color = OnPrimary)
            }
        }
    }
}
