package com.shifttracker.app.data.repository

import com.shifttracker.app.data.db.dao.ShiftDao
import com.shifttracker.app.data.model.Shift
import com.shifttracker.app.data.model.ShiftWithJob
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShiftRepository @Inject constructor(
    private val shiftDao: ShiftDao
) {
    fun getAllShifts(): Flow<List<ShiftWithJob>> = shiftDao.getAllShiftsWithJob()
    fun getShiftsInRange(startMs: Long, endMs: Long): Flow<List<ShiftWithJob>> =
        shiftDao.getShiftsInRangeWithJob(startMs, endMs)
    fun getShiftsByJob(jobId: Long): Flow<List<ShiftWithJob>> =
        shiftDao.getShiftsByJobWithJob(jobId)
    fun getRecentShifts(limit: Int = 3): Flow<List<ShiftWithJob>> =
        shiftDao.getRecentShiftsWithJob(limit)
    suspend fun insertShift(shift: Shift): Long = shiftDao.insertShift(shift)
    suspend fun updateShift(shift: Shift) = shiftDao.updateShift(shift)
    suspend fun deleteShift(shift: Shift) = shiftDao.deleteShift(shift)
    suspend fun deleteAllShifts() = shiftDao.deleteAllShifts()
}
