package io.github.andreparkh.dto.auth

data class RegisterRequest (
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String
)
