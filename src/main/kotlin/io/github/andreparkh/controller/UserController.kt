package io.github.andreparkh.controller

import io.github.andreparkh.dto.ResponseUser
import io.github.andreparkh.dto.UpdateUser
import io.github.andreparkh.model.User
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
                    schema = Schema(implementation = ResponseUser::class))]),
            ApiResponse(responseCode = "404", description = "Пользователь не найден")
        ]
    )
    fun getUserById(
        @PathVariable
        @Parameter(description = "ID пользователя", required = true)
        id: Long
    ): ResponseUser? {
        return userService.getUserById(id)
    }

    @GetMapping()
    @Operation(
        summary = "Получить всех пользователей",
        description = "Возвращает список всех зарегистрированных пользователей",
        responses = [
            ApiResponse(responseCode = "200", description = "Список пользователей успешно получен",
                content = [Content(mediaType = "application/json",
                    schema = Schema(implementation = Array<ResponseUser>::class))]),
            ApiResponse(responseCode = "404", description = "Пользователи не найдены")
        ]
    )
    fun getAllUsers(): List<ResponseUser> {
        return userService.getAllUsers()
    }

    @PutMapping("/me")
    @Operation(
        summary = "Обновить данные текущего пользователя",
        description = "Позволяет авторизованному пользователю обновить свои личные данные",
        responses = [
            ApiResponse(responseCode = "200", description = "Пользователь успешно обновлен",
                content = [Content(mediaType = "application/json",
                    schema = Schema(implementation = ResponseUser::class))]),
            ApiResponse(responseCode = "400", description = "Некорректные данные запроса или поля имеют недопустимые значения"),
            ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
        ]
    )
    fun updateUser(
        @Parameter(
            description = "Данные авторизованного пользователя. Используется для получения email текущего пользователя",
            required = true)
        authentication: Authentication,

        @RequestBody
        @Parameter(
            description = "JSON-объект с данными для обновления пользователя",
            required = true,
            content = [Content(mediaType = "application/json",
                schema = Schema(implementation = UpdateUser::class)) ]
        )
        updateUser: UpdateUser
    ): ResponseUser? {
        return userService.updateUser(authentication.name, updateUser)
    }

    @DeleteMapping("/me")
    @Operation()
    fun deleteMyAccount(
        authentication: Authentication
    ): ResponseEntity<Unit> {
        val email = authentication.name
        val deleted = userService.deleteUserByEmail(email)

        return if (deleted) ResponseEntity.ok().build()
        else ResponseEntity.notFound().build()
    }
}