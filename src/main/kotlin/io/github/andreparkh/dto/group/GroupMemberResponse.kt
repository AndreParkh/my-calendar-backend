package io.github.andreparkh.dto.group

import io.github.andreparkh.enums.GroupMemberRole
import java.time.LocalDateTime

data class GroupMemberResponse(
    val userId: Long,
    val email: String,
    val role: GroupMemberRole,
    val joinedAt: LocalDateTime
)