package com.shifttracker.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shifttracker.app.data.db.dao.JobDao
import com.shifttracker.app.data.db.dao.ShiftDao
import com.shifttracker.app.data.model.Job
import com.shifttracker.app.data.model.Shift

@Database(
    entities = [Job::class, Shift::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun jobDao(): JobDao
    abstract fun shiftDao(): ShiftDao

    companion object {
        const val DATABASE_NAME = "shift_tracker_db"
    }
}
