package io.github.andreparkh.config

object Constants {

}

object JwtConstants {
    const val AUTHORIZATION_HEADER = "Authorization"
    const val TYPE_TOKEN = "Bearer"
    const val START_POSITION_TOKEN = 7
}

object AppRoles {
    const val USER_ROLE = "USER_ROLE"
    const val ADMIN_ROLE = "ADMIN_ROLE"
}
object HttpConstants {
    const val APPLICATION_JSON = "application/json"
}

object EventParticipantStatus {
    const val ACCEPTED = "ACCEPTED"
    const val REJECTED = "REJECTED"
    const val PENDING = "PENDING"
}