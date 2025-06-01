package io.github.andreparkh.service

import io.github.andreparkh.dto.AuthResponse
import io.github.andreparkh.dto.LoginRequest
import io.github.andreparkh.dto.RegisterRequest
import io.github.andreparkh.model.User
import io.github.andreparkh.repository.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val jwtService: JwtService,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val userDetailsService: UserDetailsService
) {

    fun register(request: RegisterRequest): AuthResponse {
        val user = User(
            firstName = request.firstName,
            lastName = request.lastName,
            email = request.email,
            passwordHash = passwordEncoder.encode(request.password)
        )

        val savedUser = userRepository.save(user)
        userRepository.flush()

        return AuthResponse(jwtService.generateToken(savedUser.email))
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