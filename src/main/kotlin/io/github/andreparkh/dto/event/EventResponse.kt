package io.github.andreparkh.dto.event

import io.github.andreparkh.dto.user.ShortUserInfo
import java.time.LocalDateTime

data class EventResponse(
    val id: Long,
    val title: String,
    val description: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val createdBy: ShortUserInfo,
    val isRepeating: Boolean,
    val repeatRule: String,
    val createdAt: LocalDateTime,
    val participants: List<ShortUserInfo>
)