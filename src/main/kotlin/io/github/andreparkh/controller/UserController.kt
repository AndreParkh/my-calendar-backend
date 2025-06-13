package io.github.andreparkh.controller

import io.github.andreparkh.dto.UpdateUser
import io.github.andreparkh.model.User
import io.github.andreparkh.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/private/users")
@Tag(name = "Управление пользователями", description = "Операции связанные с управлением пользователями")
class UserController (
    private val userService: UserService
) {

    @GetMapping("/{id}")
    @Operation(
        summary = "Получить пользователя по ID",
        description = "Возвращает пользователя по его уникальному ID",
        responses = [
            ApiResponse(responseCode = "200", description = "Пользователь найден",
                content = [Content(mediaType = "application/json",
                    schema = Schema(implementation = User::class))]),
            ApiResponse(responseCode = "404", description = "Пользователь не найден")
        ]
    )
    fun getUserById(
        @PathVariable
        @Parameter(description = "ID пользователя", required = true)
        id: Long
    ): User? {
        return userService.getUserById(id)
    }

    @GetMapping()
    @Operation(
        summary = "Получить всех пользователей",
        description = "Возвращает список всех зарегистрированных пользователей",
        responses = [
            ApiResponse(responseCode = "200", description = "Список пользователей успешно получен",
                content = [Content(mediaType = "application/json",
                    schema = Schema(implementation = Array<User>::class))]),
            ApiResponse(responseCode = "404", description = "Пользователи не найдены")
        ]
    )
    fun getAllUsers(): List<User> {
        return userService.getAllUsers()
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Обновить пользователя",
        description = "Обновить данные пользователя по его ID",
        responses = [
            ApiResponse(responseCode = "200", description = "Пользователь успешно обновлен",
                content = [Content(mediaType = "application/json",
                    schema = Schema(implementation = User::class))]),
            ApiResponse(responseCode = "400", description = "Некорректные данные пользователя"),
            ApiResponse(responseCode = "404", description = "Пользователь с указанным ID не найден")
        ]
    )
    fun updateUser(
        @PathVariable
        @Parameter(description = "ID пользователя", required = true)
        id: Long,

        @RequestBody
        @Parameter(
            description = "Данные для обновления пользователя",
            required = true,
            content = [Content(mediaType = "application/json",
                schema = Schema(implementation = UpdateUser::class)) ]
        )
        updateUser: UpdateUser
    ): User? {
        return userService.updateUser(id, updateUser)
    }
}