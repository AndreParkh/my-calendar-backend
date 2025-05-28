package io.github.andreparkh.controller

import io.github.andreparkh.dto.AuthResponse
import io.github.andreparkh.dto.LoginRequest
import io.github.andreparkh.dto.RegisterRequest
import io.github.andreparkh.service.AuthService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): AuthResponse =
        authService.register(request)

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): AuthResponse =
        authService.login(request)
}