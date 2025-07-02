package io.github.andreparkh.dto.group

import java.time.LocalDateTime

data class GroupResponse(
    val id: Long,
    val name: String,
    val inviteToken: String,
    val createdAt: LocalDateTime
)