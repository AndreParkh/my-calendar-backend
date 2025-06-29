package io.github.andreparkh.model

import io.github.andreparkh.config.JwtConstants
import io.github.andreparkh.dto.group.GroupResponse
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name="groups")
data class Group (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Column
    var inviteToken: String = UUID.randomUUID().toString(),

    @Column
    var tokenExpired: LocalDateTime = LocalDateTime.now().plusHours(24),

    @Column(nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
){
    fun getId(): Long {
        require(this.id != null) { "Group ID must not be null" }
        return this.id
    }

    fun toGroupResponse() = GroupResponse(
        id = this.getId(),
        name = this.name,
        inviteToken = this.inviteToken,
        createdAt = this.createdAt
    )

    fun isTokenValid(): Boolean {
        return !this.tokenExpired.isBefore(LocalDateTime.now())
    }

    fun renewToken() {
        this.inviteToken = UUID.randomUUID().toString()
        this.tokenExpired = LocalDateTime.now().plusHours(24)
    }

}