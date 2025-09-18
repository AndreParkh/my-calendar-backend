package io.github.andreparkh.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.net.URI

@Service
class RedirectResponseBuilder(
    private val cookiePath: String = "/",
    private val headerName: String = HttpHeaders.SET_COOKIE
) {
    @Value("\${cookie.key.auth-token}")
    private lateinit var authTokenKey: String

    fun buildRedirectWithCookie(token: String, redirectUrl: String): ResponseEntity<Unit> {
        val cookie = ResponseCookie.from(authTokenKey, token)
            .path(cookiePath)
            .build()

        return ResponseEntity.status(HttpStatus.FOUND)
            .header(headerName, cookie.toString())
            .location(URI.create(redirectUrl))
            .build()
    }
}