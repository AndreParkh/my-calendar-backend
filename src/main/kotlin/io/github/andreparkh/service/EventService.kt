package io.github.andreparkh.service

import io.github.andreparkh.config.EventParticipantStatus
import io.github.andreparkh.dto.event.EventRequest
import io.github.andreparkh.dto.event.EventResponse
import io.github.andreparkh.dto.event.ParticipantResponse
import io.github.andreparkh.dto.event.UpdateParticipationStatusRequest
import io.github.andreparkh.model.Event
import io.github.andreparkh.model.EventParticipant
import io.github.andreparkh.repository.EventParticipantRepository
import io.github.andreparkh.repository.EventRepository
import io.github.andreparkh.repository.UserRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service

@Service
class EventService(
    private val eventRepository: EventRepository,
    private val participantRepository: EventParticipantRepository,
    private val userRepository: UserRepository,
    private val userService: UserService
) {
    fun createEvent(eventRequest: EventRequest): EventResponse {

        val currentUser = userService.getCurrentUser()

        val event = Event(
            title = eventRequest.title,
            description = eventRequest.description,
            startTime = eventRequest.startTime,
            endTime = eventRequest.endTime,
            createdBy = currentUser,
            isRepeatable = eventRequest.isRepeatable,
            repeateRule = eventRequest.repeateRule
        )

        val participant = EventParticipant(
            event = event,
            user = currentUser,
        )

        val savedEvent = eventRepository.save(event)
        participantRepository.save(participant)

        return savedEvent.toEventResponse()
    }

    fun getEventById(eventId: Long): EventResponse {

        val currentUser = userService.getCurrentUser()
        val event = getEventEntity(eventId)

        val isParticipant = participantRepository.existsByEventIdAndUserId(eventId, currentUser.id!!)

        if (!currentUser.isAdmin() && !isParticipant)
            throw AccessDeniedException("Доступ запрещен")

        return event.toEventResponse()
    }

    fun getAllParticipantsByEventId(eventId: Long): List<ParticipantResponse> {

        val currentUser = userService.getCurrentUser()
        getEventEntity(eventId)

        val isParticipant = participantRepository.existsByEventIdAndUserId(eventId, currentUser.id!!)

        if (!currentUser.isAdmin() && !isParticipant)
            throw AccessDeniedException("Доступ запрещен")

        val participants = participantRepository.findByEventId(eventId)

        return participants.map { it.toParticipantResponse() }
    }

    fun joinEventById(eventId: Long, userId: Long): List<ParticipantResponse> {
        val event = getEventEntity(eventId)

        val user = userRepository.findById(userId)
            .orElseThrow{ EntityNotFoundException("Пользователь с Id $userId не найден") }

        if (participantRepository.existsByEventIdAndUserId(eventId, userId))
            throw IllegalArgumentException("Пользователь уже участвует в событии")

        val participant = EventParticipant(
            event = event,
            user = user,
        )

        participantRepository.save(participant)
        return getAllParticipantsByEventId(eventId)
    }

    fun updateParticipationStatus(eventId: Long, request: UpdateParticipationStatusRequest): ParticipantResponse {
        val currentUser = userService.getCurrentUser()

        if (request.newStatus !in listOf(EventParticipantStatus.ACCEPTED, EventParticipantStatus.REJECTED))
            throw IllegalArgumentException("Некорректный статус: ${request.newStatus}")

        val existingParticipant = participantRepository.findByEventIdAndUserId(eventId, request.participantId)
            .orElseThrow { IllegalArgumentException("Пользователь не участвует в событии") }

        val ownParticipation = currentUser.id == request.participantId

        if (!currentUser.isAdmin() && !ownParticipation)
            throw AccessDeniedException("Недостаточно прав для изменения")

        existingParticipant.status = request.newStatus
        existingParticipant.updateResponse()

        return participantRepository.save(existingParticipant).toParticipantResponse()
    }


    private fun getEventEntity(id: Long): Event {
        val event = eventRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Событие с ID $id не найдено") }

        return event
    }

}