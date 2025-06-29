package io.github.andreparkh.controller

import io.github.andreparkh.config.HttpConstants
import io.github.andreparkh.dto.ErrorResponse
import io.github.andreparkh.dto.user.ChangeRoleRequest
import io.github.andreparkh.dto.user.UserResponse
import io.github.andreparkh.dto.user.UpdateUser
import io.github.andreparkh.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/private/users")
@Tag(name = "Управление пользователями", description = "API для работы с пользователями")
class UserController (
    private val userService: UserService
) {

    @GetMapping("/{id}")
    @Operation(
        summary = "Получение пользователя по ID",
        description = "Возвращает пользователя по его уникальному ID",
        responses = [
            ApiResponse(responseCode = "200", description = "Пользователь найден", content = [
                Content(mediaType = HttpConstants.APPLICATION_JSON, schema = Schema(implementation = UserResponse::class))
            ]),
            ApiResponse(responseCode = "401", description = "Пользователь не авторизован", content = [
                Content(mediaType = HttpConstants.APPLICATION_JSON, schema = Schema(implementation = ErrorResponse::class))
            ]),
            ApiResponse(responseCode = "404", description = "Пользователь не найден", content = [
                Content(schema = Schema())
            ])
        ]
    )
    fun getUserById(
        @PathVariable
        @Parameter(description = "ID пользователя", required = true)
        id: Long
    ): ResponseEntity<UserResponse> {
            return ResponseEntity.ok(userService.getUserById(id))
    }

    @GetMapping()
    @Operation(
        summary = "Получение всех пользователей",
        description = "Возвращает список всех зарегистрированных пользователей",
        responses = [
            ApiResponse(responseCode = "200", description = "Список пользователей успешно получен", content = [
                Content(mediaType = HttpConstants.APPLICATION_JSON, schema = Schema(implementation = Array<UserResponse>::class))
            ]),
            ApiResponse(responseCode = "401", description = "Пользователь не авторизован", content = [
                Content(mediaType = HttpConstants.APPLICATION_JSON, schema = Schema(implementation = ErrorResponse::class))
            ]),
        ]
    )
    fun getAllUsers(): ResponseEntity<List<UserResponse>> {
        return ResponseEntity.ok(userService.getAllUsers())
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Обновление данных пользователя",
        description = "Позволяет пользователю обновить личные данные",
        responses = [
            ApiResponse(responseCode = "200", description = "Данные пользователя успешно обновлены", content = [
                Content(mediaType = HttpConstants.APPLICATION_JSON, schema = Schema(implementation = UserResponse::class))
            ]),
            ApiResponse(responseCode = "400", description = "Некорректные данные запроса или поля имеют недопустимые значения", content = [
                Content(mediaType = HttpConstants.APPLICATION_JSON, schema = Schema(implementation = ErrorResponse::class))
            ]),
            ApiResponse(responseCode = "401", description = "Пользователь не авторизован", content = [
                Content(mediaType = HttpConstants.APPLICATION_JSON, schema = Schema(implementation = ErrorResponse::class))
            ]),
            ApiResponse(responseCode = "403", description = "Недостаточно прав для изменения", content = [
                Content(schema = Schema())
            ]),
            ApiResponse(responseCode = "404", description = "Пользователь не авторизован", content = [
                Content(schema = Schema())
            ]),
        ]
    )
    fun updateUser(
        @PathVariable
        @Parameter(description = "ID пользователя", required = true)
        id: Long,

        @RequestBody
        @Parameter(
            description = "JSON-объект с данными для обновления пользователя",
            required = true,
            content = [
                Content(mediaType = HttpConstants.APPLICATION_JSON, schema = Schema(implementation = UpdateUser::class))
            ]
        )
        updateUser: UpdateUser
    ): ResponseEntity<UserResponse> {
        return ResponseEntity.ok(userService.updateUser(id, updateUser))
    }

    @PutMapping("/{id}/role")
    @Operation(
        summary = "Изменение роли пользователя",
        description = "Позволяет администратору изменить роль пользователя по его ID",
        responses = [
            ApiResponse(responseCode = "200", description = "Роль успешно изменена"),
            ApiResponse(responseCode = "400", description = "Ошибка запроса: неверная роль"),
            ApiResponse(responseCode = "403", description = "Недостаточно прав для изменения"),
            ApiResponse(responseCode = "404", description = "Пользователь не найден")
        ]
    )
    fun changeRoleById(
        @PathVariable
        @Parameter(description = "ID пользователя, роль которого нужно изменить")
        id: Long,

        @RequestBody
        @Parameter(description = "Новая роль пользователя")
        request: ChangeRoleRequest,
    ): ResponseEntity<Unit> {
        val success = userService.changeRoleById(id, request.role)

        return if (success) ResponseEntity.ok().build()
        else ResponseEntity.badRequest().build()
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Удаление пользователя",
        description = "Позволяет администратору удалить пользователя по его уникальному идентификатору. " +
                "Обычные пользователи могут удалять только свой аккаунт",
        responses = [
            ApiResponse(responseCode = "200", description = "Пользователь успешно удален"),
            ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            ApiResponse(responseCode = "404", description = "Пользователь с указанным ID не найден")
        ]
    )
    fun deleteById(
        @PathVariable
        @Parameter(description = "ID пользователя", required = true, example = "1")
        id: Long,
    ): ResponseEntity<Unit> {
        val deleted = userService.deleteUserById(id)

        return if (deleted) ResponseEntity.ok().build()
        else ResponseEntity.notFound().build()
    }
}