package io.github.andreparkh.model

import io.github.andreparkh.enums.TastStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "tasks")
data class Task(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var title: String,

    @Column
    var description: String? = null,

    @Column
    var deadline: LocalDateTime? = null,

    @ManyToOne
    @JoinColumn(name = "created_by",nullable = false)
    val createdBy: User,

    @ManyToOne
    @JoinColumn(name = "assigned_to",nullable = false)
    var assignedTo: User,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: TastStatus? = TastStatus.NEW,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column
    var updatedAt: LocalDateTime? = null,
) {
    @PreUpdate
    fun onUpdate(){
        this.updatedAt = LocalDateTime.now()
    }
}