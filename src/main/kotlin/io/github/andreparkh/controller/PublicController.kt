package io.github.andreparkh.controller

import io.github.andreparkh.dto.user.UserResponse
import io.github.andreparkh.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/public")
@Tag(name = "Публичные API", description = "API не требующие авторизации")
class PublicController(
    private val userService: UserService
) {
    @GetMapping("/user")
    @Operation(
        summary = "Получение информации о пользователе",
        description = "Позволяет получить информацию от сервера для проверки работы",
        responses = [
            ApiResponse(responseCode = "200", description = "Пользователь получен", content = [
                Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = UserResponse::class))
            ]),
        ]
    )
    fun mockUser(): ResponseEntity<UserResponse> {
        val user = userService.mockUser()
        return ResponseEntity.ok(user.toUserResponse())
    }
}