package com.shifttracker.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shifttracker.app.data.model.Job
import com.shifttracker.app.data.model.ShiftWithJob
import com.shifttracker.app.data.repository.JobRepository
import com.shifttracker.app.data.repository.ShiftRepository
import com.shifttracker.app.utils.WageCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class JobStats(
    val job: Job,
    val totalHours: Double,
    val totalEarnings: Double,
    val shiftCount: Int
)

data class JobsUiState(
    val jobStats: List<JobStats> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class JobsViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val shiftRepository: ShiftRepository
) : ViewModel() {

    val uiState: StateFlow<JobsUiState> = combine(
        jobRepository.getAllActiveJobs(),
        shiftRepository.getAllShifts()
    ) { jobs, allShifts ->
        val stats = jobs.map { job ->
            val jobShifts = allShifts.filter { it.shift.jobId == job.id }
            JobStats(
                job = job,
                totalHours = WageCalculator.calculateTotalHoursForShifts(jobShifts),
                totalEarnings = WageCalculator.calculateTotalForShifts(jobShifts),
                shiftCount = jobShifts.size
            )
        }
        JobsUiState(jobStats = stats, isLoading = false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), JobsUiState())

    fun deleteJob(job: Job) {
        viewModelScope.launch {
            val shiftCount = jobRepository.countShiftsByJob(job.id)
            if (shiftCount > 0) {
                jobRepository.softDeleteJob(job.id) // preserve history
            } else {
                jobRepository.softDeleteJob(job.id)
            }
        }
    }

    fun saveJob(job: Job) {
        viewModelScope.launch {
            if (job.id == 0L) jobRepository.insertJob(job)
            else jobRepository.updateJob(job)
        }
    }
}
