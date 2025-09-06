package io.github.andreparkh.service

import io.github.andreparkh.dto.auth.YandexTokenResponse
import io.github.andreparkh.dto.auth.YandexUserInfo
import io.github.cdimascio.dotenv.dotenv
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class YandexOAuthService (
    private val webClient: WebClient,
    private val jwtService: JwtService
) {
    @Value("\${yandex.auth.client-id}")
    private lateinit var clientId: String

    @Value("\${yandex.auth.client-secret}")
    private lateinit var clientSecret: String

    @Value("\${yandex.auth.oauth-url}")
    private lateinit var oauthUrl: String

    @Value("\${yandex.auth.login-url}")
    private lateinit var loginUrl: String

    @Value("\${yandex.auth.redirect-url}")
    private lateinit var redirectUri: String

    @Value("\${frontend.url}")
    private lateinit var frontendUrl: String

    fun generateRedirectUri(): String {
        val baseUrl = "$oauthUrl/authorize"
        val queryParams = mapOf(
            "response_type" to "code",
            "client_id" to clientId,
            "redirect_uri" to redirectUri
        )

        val queryString = queryParams.entries.joinToString("&") { "${it.key}=${it.value}" }

        return "$baseUrl?$queryString"
    }

    fun getToken(code: String): YandexTokenResponse {
        val baseUrl = "$oauthUrl/token"
        val body = mapOf(
            "grant_type" to "authorization_code",
            "code" to code,
            "client_id" to clientId,
            "client_secret" to clientSecret
        )
            .entries
            .joinToString("&") { "${it.key}=${it.value}" }

        val response = webClient.post()
            .uri(baseUrl)
            .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
            .bodyValue(body)
            .retrieve()
            .bodyToMono(YandexTokenResponse::class.java)
            .block() ?: throw RuntimeException("Не удалось получить access токен")
        return response
    }

    fun getUser(accessToken: String): YandexUserInfo {

        val url = "$loginUrl/info?format=json"

        val response = webClient.get()
            .uri(url)
            .header(HttpHeaders.AUTHORIZATION, "OAuth $accessToken")
            .retrieve()
            .bodyToMono(YandexUserInfo::class.java)
            .block() ?: throw RuntimeException("Не удалось получить информацию о пользователе")

        return response
    }

    fun login(code: String): String {
        val tokenResponse = getToken(code)
        val user = getUser(tokenResponse.accessToken)
        val token = jwtService.generateToken(user.defaultEmail)
        val url = "$frontendUrl/auth/login?token=$token"
        return url
    }
}

