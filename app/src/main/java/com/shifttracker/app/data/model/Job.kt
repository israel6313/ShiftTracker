package com.shifttracker.app.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jobs")
data class Job(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "hourly_wage")
    val hourlyWage: Double,
    @ColumnInfo(name = "color_hex")
    val colorHex: String = "#4A90D9",
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false
)
