package io.github.andreparkh.model

import io.github.andreparkh.enums.EventParticipantStatus
import jakarta.persistence.*
import java.time.LocalDateTime


@Entity
@Table(name = "event_participants")
data class EventParticipants (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    var event: Event,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: EventParticipantStatus? = EventParticipantStatus.PENDING,

    @Column
    val responseAt: LocalDateTime? = null,
){
}