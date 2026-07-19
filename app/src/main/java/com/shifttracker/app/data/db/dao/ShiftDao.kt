package com.shifttracker.app.data.db.dao

import androidx.room.*
import com.shifttracker.app.data.model.Shift
import com.shifttracker.app.data.model.ShiftWithJob
import kotlinx.coroutines.flow.Flow

@Dao
interface ShiftDao {
    @Transaction
    @Query("""
        SELECT * FROM shifts 
        ORDER BY start_timestamp DESC
    """)
    fun getAllShiftsWithJob(): Flow<List<ShiftWithJob>>

    @Transaction
    @Query("""
        SELECT * FROM shifts 
        WHERE start_timestamp >= :startMs AND start_timestamp < :endMs
        ORDER BY start_timestamp DESC
    """)
    fun getShiftsInRangeWithJob(startMs: Long, endMs: Long): Flow<List<ShiftWithJob>>

    @Transaction
    @Query("""
        SELECT * FROM shifts 
        WHERE job_id = :jobId
        ORDER BY start_timestamp DESC
    """)
    fun getShiftsByJobWithJob(jobId: Long): Flow<List<ShiftWithJob>>

    @Transaction
    @Query("""
        SELECT * FROM shifts 
        ORDER BY start_timestamp DESC 
        LIMIT :limit
    """)
    fun getRecentShiftsWithJob(limit: Int = 3): Flow<List<ShiftWithJob>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShift(shift: Shift): Long

    @Update
    suspend fun updateShift(shift: Shift)

    @Delete
    suspend fun deleteShift(shift: Shift)

    @Query("DELETE FROM shifts")
    suspend fun deleteAllShifts()

    @Query("SELECT COUNT(*) FROM shifts WHERE job_id = :jobId")
    suspend fun countShiftsByJob(jobId: Long): Int
}
