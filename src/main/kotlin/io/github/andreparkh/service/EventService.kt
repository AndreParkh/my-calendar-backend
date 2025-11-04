package io.github.andreparkh.service

import io.github.andreparkh.config.EventErrorMessages
import io.github.andreparkh.config.EventParticipantStatus
import io.github.andreparkh.config.UserErrorMessages
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
import java.time.LocalDateTime

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
            isRepeatable = eventRequest.isRepeatable ?: false,
            repeatRule = eventRequest.repeatRule ?: ""
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

        val isParticipant = participantRepository.existsByEventIdAndUserId(eventId, currentUser.getId())

        if (!currentUser.isAdmin() && !isParticipant)
            throw AccessDeniedException(EventErrorMessages.ACCESS_DENIED)

        return event.toEventResponse()
    }

    fun getEventsDateBetween(start: LocalDateTime, end: LocalDateTime): List<EventResponse> {

        val currentUser = userService.getCurrentUser()
        val events = eventRepository.findEventByParticipantsAndDate(currentUser, start, end)

        return events.map { event -> event.toEventResponse() }
    }

    fun getAllParticipantsByEventId(eventId: Long): List<ParticipantResponse> {

        val currentUser = userService.getCurrentUser()
        getEventEntity(eventId)

        val isParticipant = participantRepository.existsByEventIdAndUserId(eventId, currentUser.getId())

        if (!currentUser.isAdmin() && !isParticipant)
            throw AccessDeniedException(EventErrorMessages.ACCESS_DENIED)

        val participants = participantRepository.findByEventId(eventId)

        return participants.map { it.toParticipantResponse() }
    }

    fun joinEventById(eventId: Long, userId: Long): List<ParticipantResponse> {
        val event = getEventEntity(eventId)

        val user = userRepository.findById(userId)
            .orElseThrow{ EntityNotFoundException(String.format(UserErrorMessages.NOT_FOUND_BY_ID, userId)) }

        if (participantRepository.existsByEventIdAndUserId(eventId, userId))
            throw IllegalArgumentException(EventErrorMessages.USER_ALREADY_PARTICIPANTS)

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
            throw IllegalArgumentException(String.format(EventErrorMessages.INVALID_STATUS, request.newStatus))

        val existingParticipant = participantRepository.findByEventIdAndUserId(eventId, request.participantId)
            .orElseThrow { IllegalArgumentException(String.format(EventErrorMessages.USER_ALREADY_PARTICIPANTS)) }

        val ownParticipation = currentUser.getId() == request.participantId

        if (!currentUser.isAdmin() && !ownParticipation)
            throw AccessDeniedException(EventErrorMessages.ACCESS_DENIED)

        existingParticipant.status = request.newStatus
        existingParticipant.updateResponse()

        return participantRepository.save(existingParticipant).toParticipantResponse()
    }


    private fun getEventEntity(id: Long): Event {
        val event = eventRepository.findById(id)
            .orElseThrow { EntityNotFoundException(String.format(EventErrorMessages.NOT_FOUND_BY_ID, id)) }

        return event
    }

}