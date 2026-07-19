package com.shifttracker.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shifttracker.app.data.model.Shift
import com.shifttracker.app.data.model.ShiftWithJob
import com.shifttracker.app.ui.theme.*
import com.shifttracker.app.utils.DateTimeUtils
import com.shifttracker.app.utils.WageCalculator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShiftCard(
    shiftWithJob: ShiftWithJob,
    onDelete: (Shift) -> Unit,
    modifier: Modifier = Modifier
) {
    val shift = shiftWithJob.shift
    val job = shiftWithJob.job
    val breakdown = remember(shift) { WageCalculator.calculate(shiftWithJob) }
    val jobColor = remember(job?.colorHex) {
        try { Color(android.graphics.Color.parseColor(job?.colorHex ?: "#4A90D9")) }
        catch (e: Exception) { Color(0xFF4A90D9) }
    }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete(shift)
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(ErrorColor)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Icon(Icons.Default.Delete, contentDescription = "מחק", tint = Color.White)
            }
        }
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: earnings + arrow
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "₪ ${ "%.0f".format(breakdown.totalPay) }",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SuccessColor
                    )
                    Icon(Icons.Default.ChevronRight, null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                }

                Spacer(Modifier.weight(1f))

                // Center: hours and time
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = DateTimeUtils.formatHours(breakdown.totalWorkedHours),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurface
                    )
                    Text(
                        text = DateTimeUtils.formatTimeRange(shift.startTimestamp, shift.endTimestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Spacer(Modifier.weight(1f))

                // Right: color strip + date + day
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = DateTimeUtils.formatDate(shift.startTimestamp),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = OnSurface
                        )
                        Text(
                            text = "יום ${DateTimeUtils.getDayOfWeekHebrew(shift.startTimestamp)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        job?.name?.let {
                            Text(it, style = MaterialTheme.typography.labelSmall, color = jobColor)
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    // Colored vertical strip
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(56.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(jobColor)
                    )
                }
            }
        }
    }
}
