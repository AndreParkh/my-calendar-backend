package io.github.andreparkh.controller

import io.github.andreparkh.dto.ErrorResponse
import io.github.andreparkh.dto.auth.AuthResponse
import io.github.andreparkh.dto.auth.LoginRequest
import io.github.andreparkh.dto.auth.RegisterRequest
import io.github.andreparkh.service.AuthService
import io.github.andreparkh.service.YandexOAuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
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
) {

    @Value("\${frontend.url}")
    private lateinit var frontendUrl: String

    @Value("\${frontend.auth-token}")
    private lateinit var authToken: String

    @PostMapping("/register")
    @Operation(
        summary = "Регистрация нового пользователя",
        description = "Создание нового пользователя в системе",
        responses = [
            ApiResponse(responseCode = "200", description = "Пользователь успешно создан", content = [
                Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = AuthResponse::class))
            ]),
            ApiResponse(responseCode = "400", description = "Пользователь с таким email уже существует", content = [
                Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = ErrorResponse::class))
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
                Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = AuthResponse::class))
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
        summary = "Инициировать аутентификацию через Яндекс OAuth",
        description = "Перенаправляет пользователя на страницу авторизации Яндекса для получения кода авторизации",
        responses = [
            ApiResponse(responseCode = "302", description = "Перенаправление на страницу авторизации Яндекса", content = [
                Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = RedirectView::class))
            ]),
            ApiResponse(
                responseCode = "500", description = "Ошибка при генерации URL перенаправления"
            )
        ]
    )
    fun getCodeFromYandexOAuth(): RedirectView {
        return RedirectView(yandexOAuthService.generateRedirectUri())
    }

    @GetMapping("/yandex/callback")
    @Operation(
        summary = "Обработка колбэка от Яндекс OAuth",
        description = "Принимает временный код авторизации от Яндекса, обменивает его на access token, " +
                "устанавливает куку с токеном аутентификации и перенаправляет пользователя на фронтенд",
        parameters = [
            Parameter(
                name = "code",
                description = "Временный код авторизации, выданный Яндексом после подтверждения пользователем доступа",
                required = true,
                `in` = ParameterIn.QUERY
            )
        ],
        responses = [
            ApiResponse(
                responseCode = "302",
                description = "Успешная аутентификация. Пользователь перенаправлен на фронтенд с установленной кукой аутентификации", content = [
            ]),
            ApiResponse(
                responseCode = "400",
                description = "Отсутствует параметр 'code' или он недействителен"
            ),
            ApiResponse(
                responseCode = "401",
                description = "Не удалось обменять код на токен (ошибка аутентификации у Яндекса)"
            ),
            ApiResponse(
                responseCode = "500",
                description = "Внутренняя ошибка сервера при обработке токена"
            )
        ]
    )
    fun getInfoFromYandexOAuth(
        @RequestParam("code") code: String
    ): ResponseEntity<Unit> {
        val token = yandexOAuthService.generateToken(code)
        val url = "$frontendUrl/user"
        val cookie = ResponseCookie.from(authToken, token)
            .path("/")
            .build()

        return ResponseEntity.status(302)
            .header("Set-Cookie", cookie.toString())
            .location(URI.create(url))
            .build()
    }
}