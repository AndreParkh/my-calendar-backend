package io.github.andreparkh.model

import io.github.andreparkh.dto.event.EventResponse
import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.LocalDateTime

@Entity
@Table(name = "events")
data class Event(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var title: String,

    @Column
    var description: String? = null,

    @Column(nullable = false)
    var startTime: LocalDateTime,

    @Column(nullable = false)
    var endTime: LocalDateTime,

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    var createdBy: User,

    @Column(nullable = false)
    var isRepeatable: Boolean? = false,

    @Column
    var repeateRule: String? = null,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    fun toEventResponse() = EventResponse(
        id = this.id,
        title = this.title,
        description = this.description,
        startTime = this.startTime,
        endTime = this.endTime,
        createdById = this.createdBy.id,
        isRepeating = this.isRepeatable,
        repeateRule = this.repeateRule,
        createdAt = this. createdAt
    )
}