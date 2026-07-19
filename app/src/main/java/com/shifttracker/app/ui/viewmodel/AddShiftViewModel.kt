package com.shifttracker.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shifttracker.app.data.model.Job
import com.shifttracker.app.data.model.Shift
import com.shifttracker.app.data.repository.JobRepository
import com.shifttracker.app.data.repository.ShiftRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddShiftViewModel @Inject constructor(
    private val shiftRepository: ShiftRepository,
    private val jobRepository: JobRepository
) : ViewModel() {

    val jobs: StateFlow<List<Job>> = jobRepository.getAllActiveJobs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun saveShift(
        jobId: Long,
        startMs: Long,
        endMs: Long,
        breakMinutes: Int,
        travelExpenses: Double,
        bonusAmount: Double,
        usedVehicle: Boolean,
        notes: String?
    ): Boolean {
        if (jobId == 0L || startMs >= endMs) return false
        shiftRepository.insertShift(
            Shift(
                jobId = jobId,
                startTimestamp = startMs,
                endTimestamp = endMs,
                breakDurationMinutes = breakMinutes,
                travelExpenses = travelExpenses,
                bonusAmount = bonusAmount,
                usedVehicle = usedVehicle,
                notes = notes?.takeIf { it.isNotBlank() }
            )
        )
        return true
    }
}
