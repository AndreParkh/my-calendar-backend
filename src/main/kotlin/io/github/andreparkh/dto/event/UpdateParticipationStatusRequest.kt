package io.github.andreparkh.dto.event

data class UpdateParticipationStatusRequest(
    val participantId: Long,
    val newStatus: String,
)