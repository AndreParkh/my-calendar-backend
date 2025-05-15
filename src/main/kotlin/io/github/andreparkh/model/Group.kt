package io.github.andreparkh.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name="groups")
data class Group (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Column
    var inviteToken: String? = null,

    @Column
    var tokenExpired: Boolean? = null,

    @Column(nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
){

}