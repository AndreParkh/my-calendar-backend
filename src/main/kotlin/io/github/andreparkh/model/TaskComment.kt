package io.github.andreparkh.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "task_comments")
class TaskComment (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "task_id",nullable = false)
    val task: Task,

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    val user: User,

    @Column(nullable = false)
    var commentText: String,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
){
}