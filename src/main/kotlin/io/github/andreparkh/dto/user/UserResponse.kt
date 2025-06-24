package io.github.andreparkh.dto.user

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class UserResponse (
    val id: Long?,
    val email: String,
    val firstName: String,
    val lastName: String,
    var role: String?,
    val avatarUrl: String?,
    val workStartTime: LocalTime?,
    val workEndTime: LocalTime?,
    val vacationStart: LocalDate?,
    val vacationEnd: LocalDate?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?

)