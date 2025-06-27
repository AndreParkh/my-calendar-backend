package io.github.andreparkh.model

import io.github.andreparkh.config.AppRoles
import io.github.andreparkh.dto.user.UserResponse
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Entity
@Table(name = "users")
data class User (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false)
    var passwordHash: String,

    @Column(nullable = false)
    var firstName: String,

    @Column(nullable = false)
    var lastName: String,

    @Column(nullable = false)
    var role: String = AppRoles.USER_ROLE,

    @Column
    var avatarUrl: String = "",

    @Column
    var workStartTime: LocalTime? = null,

    @Column
    var workEndTime: LocalTime? = null,

    @Column
    var vacationStart: LocalDate? = null,

    @Column
    var vacationEnd: LocalDate? = null,

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column
    var updatedAt: LocalDateTime? = null,
) {
    @PreUpdate
    fun onUpdate(){
        this.updatedAt = LocalDateTime.now()
    }

    fun isAdmin(): Boolean = this.role == AppRoles.ADMIN_ROLE

    fun toUserResponse(): UserResponse {
        require(this.id != null) { "User ID must not be null" }

        return UserResponse(
            id = this.id,
            email = this.email,
            firstName = this.firstName,
            lastName = this.lastName,
            role = this.role,
            avatarUrl = this.avatarUrl,
            workStartTime = this.workStartTime,
            workEndTime = this.workEndTime,
            vacationStart = this.vacationStart,
            vacationEnd = this.vacationEnd,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }

}
