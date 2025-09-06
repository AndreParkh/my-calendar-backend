package io.github.andreparkh.dto.auth

import com.fasterxml.jackson.annotation.JsonProperty


class YandexUserInfo (
    val id: String,
    @JsonProperty("first_name") val firstName: String,
    @JsonProperty("last_name") val lastName: String,
    @JsonProperty("default_email") val defaultEmail: String,
)