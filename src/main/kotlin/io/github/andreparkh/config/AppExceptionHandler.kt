package io.github.andreparkh.config

import io.github.andreparkh.dto.ErrorResponse
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.MalformedJwtException
import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class AppExceptionHandler {
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .badRequest()
            .body(ErrorResponse(message = ex.message ?: "Bad request"))
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFoundException(ex: EntityNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .notFound()
            .build()
    }

    @ExceptionHandler(JwtException::class)
    fun handleJwtException(ex: JwtException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(message = ex.message ?: "Ошибка JWT")

        return when (ex) {
            is ExpiredJwtException -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(errorResponse.copy(message = "Токен истёк"))

            is MalformedJwtException -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse.copy(message = "Неверный формат токена"))

            else -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(errorResponse)
        }
    }
}