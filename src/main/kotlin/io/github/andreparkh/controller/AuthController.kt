package io.github.andreparkh.controller

import io.github.andreparkh.config.HttpConstants
import io.github.andreparkh.dto.ErrorResponse
import io.github.andreparkh.dto.auth.AuthResponse
import io.github.andreparkh.dto.auth.LoginRequest
import io.github.andreparkh.dto.auth.RegisterRequest
import io.github.andreparkh.dto.auth.YandexUserInfo
import io.github.andreparkh.service.AuthService
import io.github.andreparkh.service.JwtService
import io.github.andreparkh.service.YandexOAuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView
import java.net.URI

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Управление авторизацией", description = "Операции связанные с управлением авторизацией")
class AuthController(
    private val authService: AuthService,
    private val yandexOAuthService: YandexOAuthService,
    private val jwtService: JwtService
) {

    @PostMapping("/register")
    @Operation(
        summary = "Регистрация нового пользователя",
        description = "Создание нового пользователя в системе",
        responses = [
            ApiResponse(responseCode = "200", description = "Пользователь успешно создан", content = [
                Content(mediaType = HttpConstants.APPLICATION_JSON, schema = Schema(implementation = AuthResponse::class))
            ]),
            ApiResponse(responseCode = "400", description = "Пользователь с таким email уже существует", content = [
                Content(mediaType = HttpConstants.APPLICATION_JSON, schema = Schema(implementation = ErrorResponse::class))
            ]),
        ]
    )
    fun register(
        @RequestBody
        @Parameter(description = "Данные пользователя", required = true)
        request: RegisterRequest
    ): ResponseEntity<AuthResponse> {
        return ResponseEntity.ok(authService.register(request))
    }

    @PostMapping("/login")
    @Operation(
        summary = "Аутентификация пользователя",
        description = "Возращает токен при успешной аутентификации",
        responses = [
            ApiResponse(responseCode = "200", description = "Успешная аутентификация. Возвращается токен доступа", content = [
                Content(mediaType = HttpConstants.APPLICATION_JSON, schema = Schema(implementation = AuthResponse::class))
            ]),
            ApiResponse(responseCode = "400", description = "Неверные учетные данные", content = [
                Content(schema = Schema())
            ]),
        ]
    )
    fun login(
        @RequestBody
        @Parameter(description = "Учетные данные пользователя", required = true)
        request: LoginRequest
    ): ResponseEntity<AuthResponse> {
        return try{
            return ResponseEntity.ok(authService.login(request))
        } catch (e: AuthenticationException) {
            ResponseEntity.badRequest().build()
        }
    }


    @GetMapping("/yandex")
    @Operation(
        summary = "Аутентификация яндекса",
        description = "Редиректит пользователя аутентификацию яндекса",
        responses = [
            ApiResponse(responseCode = "200", description = "Успешная аутентификация", content = [
                Content(mediaType = HttpConstants.APPLICATION_JSON, schema = Schema(implementation = RedirectView::class))
            ]),
        ]
    )
    fun yandexOAuthGetCode(): RedirectView {
        return RedirectView(yandexOAuthService.generateRedirectUri())
    }

    @GetMapping("/yandex/callback")
    @Operation(
        summary = "",
        description = "",
        responses = [
            ApiResponse(responseCode = "200", description = "Успешная аутентификация", content = [
                Content(mediaType = HttpConstants.APPLICATION_JSON, schema = Schema(implementation = YandexUserInfo::class))
            ]),
        ]
    )
    fun yandexOAuthGetData(
        @RequestParam("code") code: String
    ): ResponseEntity<Unit> {

        val tokenResponse = yandexOAuthService.getToken(code)
        val user = yandexOAuthService.getUser(tokenResponse.accessToken)
        val token = jwtService.generateToken(user.defaultEmail)
        val url = "http://localhost:5003/user"

        val cookie = ResponseCookie.from("authToken", token)
            .path("/")
            .build()

        return ResponseEntity.status(302)
            .header("Set-Cookie", cookie.toString())
            .location(URI.create(url))
            .build()
    }
}