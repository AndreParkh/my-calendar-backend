package io.github.andreparkh.dto.auth

import com.fasterxml.jackson.annotation.JsonProperty

data class YandexTokenResponse(
    @JsonProperty("token_type") val tokenType: String,
    @JsonProperty("access_token") val accessToken: String,
    @JsonProperty("expires_in") val expiresIn: Int,
    @JsonProperty("refresh_token") val refreshToken: String? = null,
)