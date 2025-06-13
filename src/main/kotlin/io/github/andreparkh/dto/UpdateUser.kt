package io.github.andreparkh.dto

import java.time.LocalDate
import java.time.LocalTime

data class UpdateUser (
    val firstName: String,
    val lastName: String,
    val avatarUrl: String,
    val workStartTime: LocalTime,
    val workEndTime: LocalTime,
    val vacationStart: LocalDate,
    val vacationEnd: LocalDate,
)