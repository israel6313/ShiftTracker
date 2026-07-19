package com.shifttracker.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shifttracker.app.data.model.ShiftWithJob
import com.shifttracker.app.data.repository.ShiftRepository
import com.shifttracker.app.utils.DateTimeUtils
import com.shifttracker.app.utils.WageCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class DashboardUiState(
    val greeting: String = "",
    val selectedYear: Int = LocalDate.now().year,
    val selectedMonth: Int = LocalDate.now().monthValue,
    val totalEarnings: Double = 0.0,
    val totalHours: Double = 0.0,
    val totalShifts: Int = 0,
    val recentShifts: List<ShiftWithJob> = emptyList()
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val shiftRepository: ShiftRepository
) : ViewModel() {

    private val _selectedYear = MutableStateFlow(LocalDate.now().year)
    private val _selectedMonth = MutableStateFlow(LocalDate.now().monthValue)

    val uiState: StateFlow<DashboardUiState> = combine(
        _selectedYear,
        _selectedMonth
    ) { year, month -> Pair(year, month) }
        .flatMapLatest { (year, month) ->
            val startMs = DateTimeUtils.getMonthStartEpoch(year, month)
            val endMs = DateTimeUtils.getMonthEndEpoch(year, month)
            combine(
                shiftRepository.getShiftsInRange(startMs, endMs),
                shiftRepository.getRecentShifts(3)
            ) { monthShifts, recentShifts ->
                DashboardUiState(
                    greeting = DateTimeUtils.getGreeting(),
                    selectedYear = year,
                    selectedMonth = month,
                    totalEarnings = WageCalculator.calculateTotalForShifts(monthShifts),
                    totalHours = WageCalculator.calculateTotalHoursForShifts(monthShifts),
                    totalShifts = monthShifts.size,
                    recentShifts = recentShifts
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardUiState())

    fun previousMonth() {
        viewModelScope.launch {
            val current = LocalDate.of(_selectedYear.value, _selectedMonth.value, 1)
            val prev = current.minusMonths(1)
            _selectedYear.value = prev.year
            _selectedMonth.value = prev.monthValue
        }
    }

    fun nextMonth() {
        viewModelScope.launch {
            val current = LocalDate.of(_selectedYear.value, _selectedMonth.value, 1)
            val next = current.plusMonths(1)
            _selectedYear.value = next.year
            _selectedMonth.value = next.monthValue
        }
    }
}
