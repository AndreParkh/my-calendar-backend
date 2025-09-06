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

object EventParticipantStatus {
    const val ACCEPTED = "ACCEPTED"
    const val REJECTED = "REJECTED"
    const val PENDING = "PENDING"
}

object UserErrorMessages {
    const val EMAIL_ALREADY_EXISTS = "Пользователь с таким email уже существует"
    const val NOT_FOUND_BY_ID = "Пользователь с ID %s не найден"
    const val NOT_FOUND_BY_EMAIL = "Пользователь с Email %s не найден"
    const val ACCESS_DENIED = "Недостаточно прав для изменения"
    const val INVALID_ROLE = "Неккоректная роль: %s"
    const val UNAUTHORIZED = "Пользователь не авторизован"
}

object GroupErrorMessages {
    const val NOT_FOUND_BY_ID = "Группа с ID %s не найдена"
    const val NOT_FOUND_BY_TOKEN = "Группа с таким токеном не найдена"
    const val TOKEN_INVALID = "Токен недействитен"
    const val USER_ALREADY_IN_GROUP = "Пользователь уже состоит в группе"
}

object EventErrorMessages {
    const val NOT_FOUND_BY_ID = "Событие с ID %s не найдена"
    const val ACCESS_DENIED = "Доступ запрещен"
    const val USER_ALREADY_PARTICIPANTS = "Пользователь уже участвует в событии"
    const val INVALID_STATUS = "Неккоректный статус: %s"
}

object YandexOAuthErrorMessages {
    const val NOT_RECEIVE_ACCESS_TOKEN = "Не удалось получить access токен"
    const val NOT_RECEIVE_USER_INFO = "Не удалось получить информацию о пользователе"
}