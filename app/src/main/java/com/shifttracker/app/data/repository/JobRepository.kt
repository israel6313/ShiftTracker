package com.shifttracker.app.data.repository

import com.shifttracker.app.data.db.dao.JobDao
import com.shifttracker.app.data.db.dao.ShiftDao
import com.shifttracker.app.data.model.Job
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobRepository @Inject constructor(
    private val jobDao: JobDao,
    private val shiftDao: ShiftDao
) {
    fun getAllActiveJobs(): Flow<List<Job>> = jobDao.getAllActiveJobs()
    fun getAllJobs(): Flow<List<Job>> = jobDao.getAllJobs()
    suspend fun getJobById(id: Long): Job? = jobDao.getJobById(id)
    suspend fun insertJob(job: Job): Long = jobDao.insertJob(job)
    suspend fun updateJob(job: Job) = jobDao.updateJob(job)
    suspend fun countShiftsByJob(jobId: Long): Int = shiftDao.countShiftsByJob(jobId)
    suspend fun softDeleteJob(id: Long) = jobDao.softDeleteJob(id)
    suspend fun deleteAllJobs() = jobDao.deleteAllJobs()
}
