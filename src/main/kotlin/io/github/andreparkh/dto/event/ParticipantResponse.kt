package io.github.andreparkh.dto.event

import java.time.LocalDateTime

data class ParticipantResponse(
    val id: Long?,
    val userId: Long?,
    val firstName: String,
    val lastName: String,
    val eventId: Long?,
    val status: String?,
    val responseAt: LocalDateTime?
)