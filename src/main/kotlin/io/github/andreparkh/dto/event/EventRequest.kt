package io.github.andreparkh.dto.event

import java.time.LocalDateTime

data class EventRequest(
    var title: String,
    var description: String,
    var startTime: LocalDateTime,
    var endTime: LocalDateTime,
    var isRepeatable: Boolean? = false,
    var repeateRule: String? = null
)