package com.shifttracker.app.utils

import com.shifttracker.app.data.model.ShiftWithJob
import kotlin.math.max

object WageCalculator {

    data class WageBreakdown(
        val regularHours: Double,
        val overtime125Hours: Double,
        val overtime150Hours: Double,
        val regularPay: Double,
        val overtime125Pay: Double,
        val overtime150Pay: Double,
        val travelExpenses: Double,
        val bonusAmount: Double,
        val totalPay: Double,
        val totalWorkedHours: Double
    )

    /**
     * Calculates wages dynamically at runtime using:
     * - Raw timestamps (epoch ms)
     * - Break deduction in minutes
     * - Tiered overtime rules:
     *   - Hours 1-8: 100% (regular)
     *   - Hours 9-10: 125% (first overtime tier)
     *   - Hours 11+: 150% (second overtime tier)
     * - Travel expenses and bonuses added as flat amounts
     */
    fun calculate(shiftWithJob: ShiftWithJob): WageBreakdown {
        val shift = shiftWithJob.shift
        val job = shiftWithJob.job ?: return emptyBreakdown()

        // Compute actual worked hours after deducting break time
        val grossDurationMs = shift.endTimestamp - shift.startTimestamp
        val breakMs = shift.breakDurationMinutes * 60_000L
        val netDurationMs = max(0L, grossDurationMs - breakMs)
        val netHours = netDurationMs / 3_600_000.0

        val baseWage = job.hourlyWage

        // Tier 1: First 8 hours at regular rate (100%)
        val regularHours = minOf(netHours, 8.0)
        // Tier 2: Hours 9 and 10 at 125%
        val overtime125Hours = if (netHours > 8.0) minOf(netHours - 8.0, 2.0) else 0.0
        // Tier 3: All hours beyond 10 at 150%
        val overtime150Hours = if (netHours > 10.0) netHours - 10.0 else 0.0

        val regularPay = regularHours * baseWage
        val overtime125Pay = overtime125Hours * baseWage * 1.25
        val overtime150Pay = overtime150Hours * baseWage * 1.50

        val basePay = regularPay + overtime125Pay + overtime150Pay
        val totalPay = basePay + shift.travelExpenses + shift.bonusAmount

        return WageBreakdown(
            regularHours = regularHours,
            overtime125Hours = overtime125Hours,
            overtime150Hours = overtime150Hours,
            regularPay = regularPay,
            overtime125Pay = overtime125Pay,
            overtime150Pay = overtime150Pay,
            travelExpenses = shift.travelExpenses,
            bonusAmount = shift.bonusAmount,
            totalPay = totalPay,
            totalWorkedHours = netHours
        )
    }

    fun calculateTotalForShifts(shifts: List<ShiftWithJob>): Double {
        return shifts.sumOf { calculate(it).totalPay }
    }

    fun calculateTotalHoursForShifts(shifts: List<ShiftWithJob>): Double {
        return shifts.sumOf { calculate(it).totalWorkedHours }
    }

    private fun emptyBreakdown() = WageBreakdown(
        regularHours = 0.0, overtime125Hours = 0.0, overtime150Hours = 0.0,
        regularPay = 0.0, overtime125Pay = 0.0, overtime150Pay = 0.0,
        travelExpenses = 0.0, bonusAmount = 0.0, totalPay = 0.0, totalWorkedHours = 0.0
    )
}
