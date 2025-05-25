package io.github.andreparkh.service

import io.github.andreparkh.dto.AuthResponse
import io.github.andreparkh.dto.LoginRequest
import io.github.andreparkh.dto.RegisterRequest
import io.github.andreparkh.model.User
import io.github.andreparkh.repository.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val jwtService: JwtService,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
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

        return AuthResponse(jwtService.generateTokenWithRoles(savedUser, emptyList()))
    }

    fun login(request: LoginRequest): AuthResponse {
        val autentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.email, request.password)
        )

        val userDetails = autentication.principal as org.springframework.security.core.userdetails.User
        val user = userRepository.findByEmail(userDetails.username) ?: throw UsernameNotFoundException("User not found")

        val token = jwtService.generateTokenWithRoles(user, emptyList())

        return AuthResponse(token)
    }
}