package com.shifttracker.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shifttracker.app.data.repository.JobRepository
import com.shifttracker.app.data.repository.ShiftRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val shiftRepository: ShiftRepository
) : ViewModel() {

    fun deleteAllData() {
        viewModelScope.launch {
            shiftRepository.deleteAllShifts()
            jobRepository.deleteAllJobs()
        }
    }
}
