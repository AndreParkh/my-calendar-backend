package io.github.andreparkh.controller

import io.github.andreparkh.config.HttpConstants
import io.github.andreparkh.dto.event.EventRequest
import io.github.andreparkh.dto.event.EventResponse
import io.github.andreparkh.dto.event.JoinEventRequest
import io.github.andreparkh.dto.event.ParticipantResponse
import io.github.andreparkh.dto.event.UpdateParticipationStatusRequest
import io.github.andreparkh.service.EventService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/private/events")
@Tag(name = "Управление событиями", description = "API для работы с событиями")
class EventController(
    private val eventService: EventService
) {
    //Создание события
    @PostMapping
    @Operation(
        summary = "Создание нового события",
        description = "Позволяет пользователю создать новое событие. Возвращается созданное событие с уникальным ID",
        responses = [
            ApiResponse(responseCode = "201", description = "Событие успешно создано", content = [
                Content(mediaType = HttpConstants.APPLICATION_JSON, schema = Schema(implementation = EventResponse::class))
            ]),
            ApiResponse(responseCode = "404", description = "Пользователь не найден")
        ]
    )
    fun createEvent(
        @RequestBody
        @Parameter(description = "Данные для создания события", required = true)
        eventRequest: EventRequest,
    ): ResponseEntity<Any> {
        val event = eventService.createEvent(eventRequest)
        return ResponseEntity.created(URI("/api/private/events/${event.id}")).body(event)
    }

    //Получение события по ID
    @GetMapping("/{eventId}")
    @Operation(
        summary = "Получение информации о событии",
        description = "Возвращает информацию о событии, по его уникальному ID",
        responses = [
            ApiResponse(responseCode = "200", description = "Информация о событии", content = [
                Content(mediaType = HttpConstants.APPLICATION_JSON, schema = Schema(implementation = EventResponse::class))
            ]),
            ApiResponse(responseCode = "403", description = "Доступ запрещен", content = []),
            ApiResponse(responseCode = "404", description = "Событие не найдено", content = [])
        ]
    )
    fun getEventById(
        @PathVariable
        @Parameter(description = "ID события", example = "1")
        eventId: Long,
    ): ResponseEntity<Any> {
        val event = eventService.getEventById(eventId)
        return ResponseEntity.ok(event)
    }

    // Получение всех участников события
    @GetMapping("/{id}/participants")
    @Operation(
        summary = "Получение списка участников события",
        description = "Возвращает список участников указанного события",
        responses = [
            ApiResponse(responseCode = "200", description = "Список участников события", content = [
                Content(mediaType = HttpConstants.APPLICATION_JSON, schema = Schema(implementation = Array<ParticipantResponse>::class))
            ]),
            ApiResponse(responseCode = "403", description = "Доступ запрещен", content = []),
            ApiResponse(responseCode = "404", description = "Событие не найдено", content = [])
        ]
    )
    fun getAllParticipantsByEventId(
        @PathVariable
        @Parameter(description = "ID события", example = "1")
        id: Long,
    ): ResponseEntity<Any> {
        val participants = eventService.getAllParticipantsByEventId(id)
        return ResponseEntity.ok(participants)
    }

    // Добавление участника события
    @PostMapping("/{eventId}/join")
    @Operation(
        summary = "Приглашение к участию в событие",
        description = "Позволяет пригласить пользователя в событие",
        responses = [
            ApiResponse(responseCode = "200", description = "Приглашение отправлено", content = [
                Content(mediaType = HttpConstants.APPLICATION_JSON, schema = Schema(implementation = Array<ParticipantResponse>::class))
            ]),
            ApiResponse(responseCode = "400", description = "Некорректные данные", content = []),
            ApiResponse(responseCode = "404", description = "Событие или пользователь не найдены", content = [])
        ]
    )
    fun joinEvent(
        @PathVariable
        @Parameter(description = "ID события", example = "1")
        eventId: Long,

        @RequestBody
        @Parameter(description = "Данные для приглашения участника", required = true)
        joinEventRequest: JoinEventRequest,
    ): ResponseEntity<Any> {
        val participants = eventService.joinEventById(eventId, joinEventRequest.userId)
        return ResponseEntity.ok(participants)
    }

    // Обновление статуса участия
    @PutMapping("/{eventId}/update-status")
    @Operation(
        summary = "Обновление статуса участия",
        description = "Позволяет пользователю принять или отклонить участие в событии",
        responses = [
            ApiResponse(responseCode = "200", description = "Статус участника успешно обновлен", content = [
                Content(mediaType = HttpConstants.APPLICATION_JSON, schema = Schema(implementation = ParticipantResponse::class))
            ]),
            ApiResponse(responseCode = "400", description = "Некорректный статус или пользователь не состоит в событии", content = []),
            ApiResponse(responseCode = "404", description = "Событие или пользователь не найдены", content = [])
        ]
    )
    fun updateStatus(
        @PathVariable
        @Parameter(description = "ID события", example = "1")
        eventId: Long,

        @RequestBody
        @Parameter(description = "Данные для обновления статуса", required = true)
        request: UpdateParticipationStatusRequest
    ): ResponseEntity<Any> {
        val updatedParticipant = eventService.updateParticipationStatus(eventId, request)
        return ResponseEntity.ok(updatedParticipant)
    }

}