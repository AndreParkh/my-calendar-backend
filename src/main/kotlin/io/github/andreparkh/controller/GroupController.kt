package io.github.andreparkh.controller

import io.github.andreparkh.dto.ErrorResponse
import io.github.andreparkh.dto.group.*
import io.github.andreparkh.service.GroupService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/private/groups")
@Tag(name = "Управление группами", description = "API для работы с группами пользователей")
class GroupController (
  private val groupService: GroupService
) {
    @PostMapping()
    @Operation(
        summary = "Создание новой группы",
        description = "Позволяет пользователю создать новую групп. Возвращается созданная группа с уникальным ID",
        responses = [
            ApiResponse(responseCode = "201", description = "Группа успешно создана", content = [
                Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = GroupResponse::class))
            ]),
            ApiResponse(responseCode = "403", description = "Доступ запрещен", content = [
                Content(schema = Schema())
            ])
        ]
    )
    fun createGroup(
        @RequestBody
        @Parameter(description = "Данные для создания группы", required = true)
        createGroupRequest: CreateGroupRequest,
    ): ResponseEntity<GroupResponse> {
        val createdGroup = groupService.createGroup(createGroupRequest)
        return ResponseEntity.created(URI("/api/private/groups/${createdGroup.id}")).body(createdGroup)
    }

    @GetMapping("/{groupId}")
    @Operation(
        summary = "Получение информации о группе",
        description = "Возвращает информацию о группе по ее уникальному ID",
        responses = [
            ApiResponse(responseCode = "200", description = "Информация о группе", content = [
                Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = GroupResponse::class))
            ]),
            ApiResponse(responseCode = "403", description = "Доступ запрещен", content = [
                Content(schema = Schema())
            ]),
            ApiResponse(responseCode = "404", description = "Группа не найдена", content = [
                Content(schema = Schema())
            ])
        ]
    )
    fun getGroupById(
        @PathVariable
        @Parameter(description = "ID группы", example = "1")
        groupId: Long
    ): ResponseEntity<GroupResponse> {
        val group = groupService.getGroupById(groupId)
        return ResponseEntity.ok(group)
    }

    @PostMapping("/join")
    @Operation(
        summary = "Присоединение к группе",
        description = "Позволяет пользователю присоединиться к группе по пригласительному токену",
        responses = [
            ApiResponse(responseCode = "200", description = "Успешное присоединение к группе", content = [
                Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = GroupResponse::class))
            ]),
            ApiResponse(responseCode = "400", description = "Неверный токен или пользователь уже состоит в группе", content = [
                Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = ErrorResponse::class))
            ]),
            ApiResponse(responseCode = "403", description = "Доступ запрещен", content = [
                Content(schema = Schema())
            ]),
            ApiResponse(responseCode = "404", description = "Группа не найдена", content = [
                Content(schema = Schema())
            ])
        ]
    )
    fun joinGroup(
        @RequestBody
        @Parameter(description = "Пригласительный токен", required = true)
        joinGroupRequest: JoinGroupRequest,
    ): ResponseEntity<GroupResponse> {
        val joinedGroup = groupService.joinGroup(joinGroupRequest.inviteToken)
        return ResponseEntity.ok(joinedGroup)
    }

    @GetMapping("/{groupId}/members")
    @Operation(
        summary = "Получение списка участников группы",
        description = "Возвращает список всех участников указанной группы",
        responses = [
            ApiResponse(responseCode = "200", description = "Список участников группы", content = [
                Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = Array<GroupMemberResponse>::class))
            ]),
            ApiResponse(responseCode = "403", description = "Доступ запрещен", content = [
                Content(schema = Schema())
            ]),
            ApiResponse(responseCode = "404", description = "Группа не найдена", content = [
                Content(schema = Schema())
            ])
        ]
    )
    fun getListMembers(
        @PathVariable
        @Parameter(description = "ID группы", example = "1")
        groupId: Long
    ): ResponseEntity<List<GroupMemberResponse>> {
        val members = groupService.getAllGroupMembersByGroupId(groupId)
        return ResponseEntity.ok(members)
    }

}