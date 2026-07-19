package com.shifttracker.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shifttracker.app.ui.theme.*
import com.shifttracker.app.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    userName: String,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    var showDeleteConfirm1 by remember { mutableStateOf(false) }
    var showDeleteConfirm2 by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))

        // Profile card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Surface),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.weight(1f))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        userName.ifBlank { "ישראל" },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                    Text("Smart App", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
                Spacer(Modifier.width(16.dp))
                // Avatar placeholder
                Surface(
                    modifier = Modifier.size(60.dp).clip(CircleShape),
                    color = Primary
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            userName.firstOrNull()?.uppercase() ?: "י",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = OnPrimary
                        )
                    }
                }
            }
        }

        // System settings block
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column {
                SettingsRow(icon = Icons.Default.MonetizationOn, title = "מטבע ברירת מחדל", value = "שקל (₪)") {}
                Divider(color = DividerColor, modifier = Modifier.padding(horizontal = 16.dp))
                SettingsRow(icon = Icons.Default.CalendarToday, title = "תחילת שבוע עבודה", value = "יום ראשון") {}
            }
        }

        // Data management block
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column {
                SettingsRow(icon = Icons.Default.Upload, title = "גיבוי וייצוא נתונים", value = "") {}
                Divider(color = DividerColor, modifier = Modifier.padding(horizontal = 16.dp))
                SettingsRow(icon = Icons.Default.Download, title = "שחזור נתונים מקובץ", value = "") {}
            }
        }

        Spacer(Modifier.weight(1f))

        // Danger zone
        TextButton(
            onClick = { showDeleteConfirm1 = true },
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Text("מחיקת כל הנתונים מהמכשיר", color = ErrorColor, style = MaterialTheme.typography.bodyMedium)
        }
    }

    // Double confirmation dialogs
    if (showDeleteConfirm1) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm1 = false },
            title = { Text("מחיקת נתונים", color = OnSurface) },
            text = { Text("פעולה זו תמחק לצמיתות את כל המשמרות והעבודות. האם להמשיך?", color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = { showDeleteConfirm1 = false; showDeleteConfirm2 = true }) {
                    Text("המשך", color = ErrorColor)
                }
            },
            dismissButton = { TextButton(onClick = { showDeleteConfirm1 = false }) { Text("ביטול", color = Primary) } },
            containerColor = Surface
        )
    }

    if (showDeleteConfirm2) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm2 = false },
            title = { Text("אישור סופי", color = ErrorColor) },
            text = { Text("אין דרך חזרה. כל הנתונים יימחקו לצמיתות.", color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteAllData(); showDeleteConfirm2 = false }) {
                    Text("מחק הכל", color = ErrorColor, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { showDeleteConfirm2 = false }) { Text("ביטול", color = Primary) } },
            containerColor = Surface
        )
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.ChevronRight, null, tint = TextSecondary, modifier = Modifier.size(18.dp))
            if (value.isNotBlank()) {
                Spacer(Modifier.width(4.dp))
                Text(value, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(title, color = OnSurface, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.width(8.dp))
            Icon(icon, null, tint = Primary, modifier = Modifier.size(20.dp))
        }
    }
}
