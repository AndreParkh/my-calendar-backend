package io.github.andreparkh.model

import io.github.andreparkh.config.EventParticipantStatus
import io.github.andreparkh.dto.event.ParticipantResponse
import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.LocalDateTime


@Entity
@Table(name = "event_participants")
data class EventParticipant (
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

    @Column(nullable = false)
    var status: String = EventParticipantStatus.PENDING,

    @Column
    var responseAt: LocalDateTime? = null,
) {

    fun getId(): Long {
        require(this.id != null) { "Event participant ID must not be null" }
        return this.id
    }

    fun updateResponse() {
        this.responseAt = LocalDateTime.now()
    }

    fun toParticipantResponse() = ParticipantResponse(
        id = this.getId(),
        userId = this.user.getId(),
        firstName = this.user.firstName,
        lastName = this.user.lastName,
        eventId = this.event.getId(),
        status = this.status,
        responseAt = this.responseAt
    )
}