package io.github.andreparkh.controller

import io.github.andreparkh.service.RedirectResponseBuilder
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
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView

@RestController
@RequestMapping("/api/auth/yandex")
@Tag(name = "Управеление авторизацией Яндекса", description = "Операции связанные OAuth Яндекс ID" )
class YandexOAuthController(
    private val redirectResponseBuilder: RedirectResponseBuilder,
    private val yandexOAuthService: YandexOAuthService,
) {
    @Value("\${frontend.url}")
    private lateinit var frontendUrl: String

    @GetMapping("")
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

    @GetMapping("/callback")
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
        val redirectUrl = "$frontendUrl/user"

        return redirectResponseBuilder.buildRedirectWithCookie(token, redirectUrl)
    }
}