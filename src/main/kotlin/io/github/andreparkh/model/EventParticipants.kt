package io.github.andreparkh.model

import io.github.andreparkh.enums.EventParticipantStatus
import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.LocalDateTime


@Entity
@Table(name = "event_participants")
data class EventParticipants (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    var event: Event,

    @OnDelete(action = OnDeleteAction.CASCADE)
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