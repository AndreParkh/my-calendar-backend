package io.github.andreparkh.repository

import io.github.andreparkh.model.EventParticipant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface EventParticipantRepository: JpaRepository<EventParticipant, Long> {
    fun findByEventId(eventId: Long): List<EventParticipant>
    fun findByEventIdAndUserId(eventId: Long, userId: Long): Optional<EventParticipant>
    fun existsByEventIdAndUserId(eventId: Long, userId: Long): Boolean
}