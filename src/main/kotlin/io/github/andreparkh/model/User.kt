package io.github.andreparkh.model

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

    @Column
    var avatarUrl: String? = null,

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
    var updatedAt: LocalDateTime? = null
) {
    @PreUpdate
    fun onUpdate(){
        this.updatedAt = LocalDateTime.now()
    }
}
