package com.shifttracker.app.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "shifts",
    foreignKeys = [
        ForeignKey(
            entity = Job::class,
            parentColumns = ["id"],
            childColumns = ["job_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["job_id"])]
)
data class Shift(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "job_id")
    val jobId: Long,
    @ColumnInfo(name = "start_timestamp")
    val startTimestamp: Long,
    @ColumnInfo(name = "end_timestamp")
    val endTimestamp: Long,
    @ColumnInfo(name = "break_duration_minutes")
    val breakDurationMinutes: Int = 0,
    @ColumnInfo(name = "travel_expenses")
    val travelExpenses: Double = 0.0,
    @ColumnInfo(name = "bonus_amount")
    val bonusAmount: Double = 0.0,
    @ColumnInfo(name = "used_vehicle")
    val usedVehicle: Boolean = false,
    val notes: String? = null
)
