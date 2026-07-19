package com.shifttracker.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shifttracker.app.data.model.Job
import com.shifttracker.app.data.model.Shift
import com.shifttracker.app.data.model.ShiftWithJob
import com.shifttracker.app.data.repository.JobRepository
import com.shifttracker.app.data.repository.ShiftRepository
import com.shifttracker.app.utils.DateTimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class ShiftsUiState(
    val shifts: List<ShiftWithJob> = emptyList(),
    val jobs: List<Job> = emptyList(),
    val selectedJobId: Long? = null,
    val selectedYear: Int = LocalDate.now().year,
    val selectedMonth: Int = LocalDate.now().monthValue,
    val isLoading: Boolean = false
)

@HiltViewModel
class ShiftsViewModel @Inject constructor(
    private val shiftRepository: ShiftRepository,
    private val jobRepository: JobRepository
) : ViewModel() {

    private val _selectedJobId = MutableStateFlow<Long?>(null)
    private val _selectedYear = MutableStateFlow(LocalDate.now().year)
    private val _selectedMonth = MutableStateFlow(LocalDate.now().monthValue)

    val uiState: StateFlow<ShiftsUiState> = combine(
        _selectedJobId, _selectedYear, _selectedMonth
    ) { jobId, year, month -> Triple(jobId, year, month) }
        .flatMapLatest { (jobId, year, month) ->
            val startMs = DateTimeUtils.getMonthStartEpoch(year, month)
            val endMs = DateTimeUtils.getMonthEndEpoch(year, month)
            val shiftsFlow = if (jobId != null)
                shiftRepository.getShiftsByJob(jobId)
            else
                shiftRepository.getShiftsInRange(startMs, endMs)
            combine(shiftsFlow, jobRepository.getAllActiveJobs()) { shifts, jobs ->
                ShiftsUiState(
                    shifts = shifts,
                    jobs = jobs,
                    selectedJobId = jobId,
                    selectedYear = year,
                    selectedMonth = month
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ShiftsUiState())

    fun filterByJob(jobId: Long?) { _selectedJobId.value = jobId }
    fun previousMonth() {
        val prev = LocalDate.of(_selectedYear.value, _selectedMonth.value, 1).minusMonths(1)
        _selectedYear.value = prev.year; _selectedMonth.value = prev.monthValue
    }
    fun nextMonth() {
        val next = LocalDate.of(_selectedYear.value, _selectedMonth.value, 1).plusMonths(1)
        _selectedYear.value = next.year; _selectedMonth.value = next.monthValue
    }
    fun deleteShift(shift: Shift) { viewModelScope.launch { shiftRepository.deleteShift(shift) } }
    fun restoreShift(shift: Shift) { viewModelScope.launch { shiftRepository.insertShift(shift) } }
}
