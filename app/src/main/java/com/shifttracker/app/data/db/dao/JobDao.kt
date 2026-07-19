package com.shifttracker.app.data.db.dao

import androidx.room.*
import com.shifttracker.app.data.model.Job
import kotlinx.coroutines.flow.Flow

@Dao
interface JobDao {
    @Query("SELECT * FROM jobs WHERE is_deleted = 0 ORDER BY name ASC")
    fun getAllActiveJobs(): Flow<List<Job>>

    @Query("SELECT * FROM jobs ORDER BY name ASC")
    fun getAllJobs(): Flow<List<Job>>

    @Query("SELECT * FROM jobs WHERE id = :id")
    suspend fun getJobById(id: Long): Job?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: Job): Long

    @Update
    suspend fun updateJob(job: Job)

    // Soft delete to preserve shift history
    @Query("UPDATE jobs SET is_deleted = 1 WHERE id = :id")
    suspend fun softDeleteJob(id: Long)

    @Query("DELETE FROM jobs")
    suspend fun deleteAllJobs()
}
