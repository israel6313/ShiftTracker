package com.shifttracker.app.utils

import java.time.*
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateTimeUtils {
    private val hebrewLocale = Locale("iw", "IL")

    fun epochToLocalDateTime(epochMs: Long): LocalDateTime =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMs), ZoneId.systemDefault())

    fun localDateTimeToEpoch(ldt: LocalDateTime): Long =
        ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    fun formatDate(epochMs: Long): String {
        val ldt = epochToLocalDateTime(epochMs)
        return ldt.format(DateTimeFormatter.ofPattern("dd.MM.yy"))
    }

    fun formatTime(epochMs: Long): String {
        val ldt = epochToLocalDateTime(epochMs)
        return ldt.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    fun formatTimeRange(startMs: Long, endMs: Long): String {
        return "${formatTime(startMs)} - ${formatTime(endMs)}"
    }

    fun getDayOfWeekHebrew(epochMs: Long): String {
        val ldt = epochToLocalDateTime(epochMs)
        return when (ldt.dayOfWeek) {
            DayOfWeek.SUNDAY -> "ראשון"
            DayOfWeek.MONDAY -> "שני"
            DayOfWeek.TUESDAY -> "שלישי"
            DayOfWeek.WEDNESDAY -> "רביעי"
            DayOfWeek.THURSDAY -> "חמישי"
            DayOfWeek.FRIDAY -> "שישי"
            DayOfWeek.SATURDAY -> "שבת"
        }
    }

    fun getMonthStartEpoch(year: Int, month: Int): Long {
        return LocalDate.of(year, month, 1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant().toEpochMilli()
    }

    fun getMonthEndEpoch(year: Int, month: Int): Long {
        val lastDay = LocalDate.of(year, month, 1).withDayOfMonth(
            LocalDate.of(year, month, 1).lengthOfMonth()
        )
        return lastDay.atTime(23, 59, 59)
            .atZone(ZoneId.systemDefault())
            .toInstant().toEpochMilli()
    }

    fun getHebrewMonthName(month: Int): String {
        return when (month) {
            1 -> "ינואר"; 2 -> "פברואר"; 3 -> "מרץ"
            4 -> "אפריל"; 5 -> "מאי"; 6 -> "יוני"
            7 -> "יולי"; 8 -> "אוגוסט"; 9 -> "ספטמבר"
            10 -> "אוקטובר"; 11 -> "נובמבר"; 12 -> "דצמבר"
            else -> ""
        }
    }

    fun formatHours(hours: Double): String {
        val h = hours.toInt()
        val m = ((hours - h) * 60).toInt()
        return if (m == 0) "${h}ש'" else "${h}ש' ${m}ד'"
    }

    fun getGreeting(): String {
        return when (LocalTime.now().hour) {
            in 5..11 -> "בוקר טוב"
            in 12..16 -> "צהריים טובים"
            in 17..20 -> "ערב טוב"
            else -> "לילה טוב"
        }
    }
}
