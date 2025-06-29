package io.github.andreparkh.controller

import io.github.andreparkh.config.HttpConstants
import io.github.andreparkh.dto.ErrorResponse
import io.github.andreparkh.dto.event.*
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
    @PostMapping
    @Operation(
        summary = "Создание нового события",
        description = "Позволяет пользователю создать новое событие. Возвращается созданное событие с уникальным ID",
        responses = [
            ApiResponse(responseCode = "201", description = "Событие успешно создано", content = [
                Content(mediaType = HttpConstants.APPLICATION_JSON, schema = Schema(implementation = EventResponse::class))
            ]),
            ApiResponse(responseCode = "404", description = "Пользователь не найден", content = [
                Content(schema = Schema())
            ])
        ]
    )
    fun createEvent(
        @RequestBody
        @Parameter(description = "Данные для создания события", required = true)
        eventRequest: EventRequest,
    ): ResponseEntity<EventResponse> {
        val event = eventService.createEvent(eventRequest)
        return ResponseEntity.created(URI("/api/private/events/${event.id}")).body(event)
    }


    @GetMapping("/{eventId}")
    @Operation(
        summary = "Получение информации о событии",
        description = "Возвращает информацию о событии, по его уникальному ID",
        responses = [
            ApiResponse(responseCode = "200", description = "Информация о событии", content = [
                Content(mediaType = HttpConstants.APPLICATION_JSON, schema = Schema(implementation = EventResponse::class))
            ]),
            ApiResponse(responseCode = "403", description = "Доступ запрещен", content = [
                Content(schema = Schema(implementation = ErrorResponse::class))
            ]),
            ApiResponse(responseCode = "404", description = "Событие не найдено", content = [
                Content(schema = Schema())
            ])
        ]
    )
    fun getEventById(
        @PathVariable
        @Parameter(description = "ID события", example = "1")
        eventId: Long,
    ): ResponseEntity<EventResponse> {
        val event = eventService.getEventById(eventId)
        return ResponseEntity.ok(event)
    }


    @GetMapping("/{id}/participants")
    @Operation(
        summary = "Получение списка участников события",
        description = "Возвращает список участников указанного события",
        responses = [
            ApiResponse(responseCode = "200", description = "Список участников события", content = [
                Content(mediaType = HttpConstants.APPLICATION_JSON, schema = Schema(implementation = Array<ParticipantResponse>::class))
            ]),
            ApiResponse(responseCode = "403", description = "Доступ запрещен", content = [
                Content(schema = Schema())
            ]),
            ApiResponse(responseCode = "404", description = "Событие не найдено", content = [
                Content(schema = Schema())
            ])
        ]
    )
    fun getAllParticipantsByEventId(
        @PathVariable
        @Parameter(description = "ID события", example = "1")
        id: Long,
    ): ResponseEntity<List<ParticipantResponse>> {
        val participants = eventService.getAllParticipantsByEventId(id)
        return ResponseEntity.ok(participants)
    }


    @PostMapping("/{eventId}/join")
    @Operation(
        summary = "Приглашение к участию в событие",
        description = "Позволяет пригласить пользователя в событие",
        responses = [
            ApiResponse(responseCode = "200", description = "Приглашение отправлено", content = [
                Content(mediaType = HttpConstants.APPLICATION_JSON, schema = Schema(implementation = Array<ParticipantResponse>::class))
            ]),
            ApiResponse(responseCode = "400", description = "Некорректные данные", content = [
                Content(mediaType = HttpConstants.APPLICATION_JSON, schema = Schema(implementation = ErrorResponse::class))
            ]),
            ApiResponse(responseCode = "404", description = "Событие или пользователь не найдены", content = [
                Content(schema = Schema())
            ])
        ]
    )
    fun joinEvent(
        @PathVariable
        @Parameter(description = "ID события", example = "1")
        eventId: Long,

        @RequestBody
        @Parameter(description = "Данные для приглашения участника", required = true)
        joinEventRequest: JoinEventRequest,
    ): ResponseEntity<List<ParticipantResponse>> {
        val participants = eventService.joinEventById(eventId, joinEventRequest.userId)
        return ResponseEntity.ok(participants)
    }


    @PutMapping("/{eventId}/update-status")
    @Operation(
        summary = "Обновление статуса участия",
        description = "Позволяет пользователю принять или отклонить участие в событии",
        responses = [
            ApiResponse(responseCode = "200", description = "Статус участника успешно обновлен", content = [
                Content(mediaType = HttpConstants.APPLICATION_JSON, schema = Schema(implementation = ParticipantResponse::class))
            ]),
            ApiResponse(responseCode = "400", description = "Некорректный статус или пользователь не состоит в событии", content = [
                Content(mediaType = HttpConstants.APPLICATION_JSON, schema = Schema(implementation = ErrorResponse::class))
            ]),
            ApiResponse(responseCode = "404", description = "Событие или пользователь не найдены", content = [
                Content(schema = Schema())
            ])
        ]
    )
    fun updateStatus(
        @PathVariable
        @Parameter(description = "ID события", example = "1")
        eventId: Long,

        @RequestBody
        @Parameter(description = "Данные для обновления статуса", required = true)
        request: UpdateParticipationStatusRequest
    ): ResponseEntity<ParticipantResponse> {
        val updatedParticipant = eventService.updateParticipationStatus(eventId, request)
        return ResponseEntity.ok(updatedParticipant)
    }

}