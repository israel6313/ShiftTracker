package com.shifttracker.app.utils

import android.content.Context
import com.shifttracker.app.data.model.ShiftWithJob

object ExportUtils {
    // Placeholder implementation as standard part of util suite
    fun exportToCsv(context: Context, shifts: List<ShiftWithJob>): String {
        val builder = StringBuilder()
        builder.append("תאריך,יום,עבודה,שעת התחלה,שעת סיום,שעות עבודה,שכר שעתי,נסיעות,בונוס,סה\"כ שכר\n")
        shifts.forEach { shiftWithJob ->
            val shift = shiftWithJob.shift
            val job = shiftWithJob.job
            if (job != null) {
                val breakdown = WageCalculator.calculate(shiftWithJob)
                builder.append("${DateTimeUtils.formatDate(shift.startTimestamp)},")
                builder.append("${DateTimeUtils.getDayOfWeekHebrew(shift.startTimestamp)},")
                builder.append("${job.name},")
                builder.append("${DateTimeUtils.formatTime(shift.startTimestamp)},")
                builder.append("${DateTimeUtils.formatTime(shift.endTimestamp)},")
                builder.append("${breakdown.totalWorkedHours},")
                builder.append("${job.hourlyWage},")
                builder.append("${shift.travelExpenses},")
                builder.append("${shift.bonusAmount},")
                builder.append("${breakdown.totalPay}\n")
            }
        }
        return builder.toString()
    }
}
