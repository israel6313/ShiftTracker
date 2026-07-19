package com.shifttracker.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shifttracker.app.ui.components.AddShiftSheet
import com.shifttracker.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMenuSheet(onDismiss: () -> Unit) {
    var showManualSheet by remember { mutableStateOf(false) }
    var showClockInConfirm by remember { mutableStateOf(false) }

    if (showManualSheet) {
        AddShiftSheet(onDismiss = { showManualSheet = false; onDismiss() })
        return
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "מה ברצונך לעשות?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = OnSurface
            )
            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Clock In card
                Card(
                    modifier = Modifier.weight(1f).height(140.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
                    shape = RoundedCornerShape(20.dp),
                    onClick = { showClockInConfirm = true }
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Timer, null, tint = Primary, modifier = Modifier.size(40.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("כניסה לעבודה", fontWeight = FontWeight.SemiBold, color = OnSurface, textAlign = TextAlign.Center)
                        Text("מד זמן חי", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                }

                // Manual add card
                Card(
                    modifier = Modifier.weight(1f).height(140.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
                    shape = RoundedCornerShape(20.dp),
                    onClick = { showManualSheet = true }
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.CalendarMonth, null, tint = Primary, modifier = Modifier.size(40.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("הוספת משמרת ידנית", fontWeight = FontWeight.SemiBold, color = OnSurface, textAlign = TextAlign.Center)
                        Text("הזנה ידנית", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                }
            }
        }
    }

    if (showClockInConfirm) {
        AlertDialog(
            onDismissRequest = { showClockInConfirm = false },
            title = { Text("כניסה לעבודה", color = OnSurface) },
            text = { Text("מד הזמן יתחיל לרוץ ברקע. תוכל לסיים את המשמרת מסרגל ההתראות.", color = TextSecondary) },
            confirmButton = { TextButton(onClick = { showClockInConfirm = false; onDismiss() }) { Text("התחל", color = Primary) } },
            dismissButton = { TextButton(onClick = { showClockInConfirm = false }) { Text("ביטול", color = TextSecondary) } },
            containerColor = Surface
        )
    }
}
