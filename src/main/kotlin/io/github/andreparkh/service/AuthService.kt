package io.github.andreparkh.service

import io.github.andreparkh.dto.auth.AuthResponse
import io.github.andreparkh.dto.auth.LoginRequest
import io.github.andreparkh.dto.auth.RegisterRequest
import io.github.andreparkh.model.User
import io.github.andreparkh.repository.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager,
    private val userDetailsService: UserDetailsService,
    private val userService: UserService
) {

    fun register(request: RegisterRequest): AuthResponse {

        val user = userService.createUser(request)

        return AuthResponse(jwtService.generateToken(user.email))
    }

    fun login(request: LoginRequest): AuthResponse {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.email, request.password)
        )

        val userDetails = userDetailsService.loadUserByUsername(request.email)
        val token = jwtService.generateToken(userDetails.username)
        return AuthResponse(token)
    }
}