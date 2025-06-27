package io.github.andreparkh.dto.event

import java.time.LocalDateTime

data class EventResponse(
    val id: Long?,
    val title: String,
    val description: String?,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val createdById: Long?,
    val isRepeating: Boolean?,
    val repeateRule: String?,
    val createdAt: LocalDateTime
)