package io.github.andreparkh.repository

import io.github.andreparkh.model.Event
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EventRepository: JpaRepository<Event, Long> {
}