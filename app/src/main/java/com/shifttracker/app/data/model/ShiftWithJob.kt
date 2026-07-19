package com.shifttracker.app.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class ShiftWithJob(
    @Embedded val shift: Shift,
    @Relation(
        parentColumn = "job_id",
        entityColumn = "id"
    )
    val job: Job?
)
