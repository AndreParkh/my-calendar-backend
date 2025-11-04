package io.github.andreparkh.repository

import io.github.andreparkh.model.Event
import io.github.andreparkh.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface EventRepository: JpaRepository<Event, Long> {

    @Query(
        """
        SELECT DISTINCT e
        FROM Event e
        JOIN EventParticipant ep ON e.id = ep.event.id
        WHERE ep.user = :user
            AND e.startTime >= :start
            AND e.startTime < :end
        """
    )
    fun findEventByParticipantsAndDate(
        @Param("user") user: User,
        @Param("start") start: LocalDateTime,
        @Param("end") end: LocalDateTime,
    ): List<Event>

}