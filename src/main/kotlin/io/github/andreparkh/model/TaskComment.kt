package io.github.andreparkh.model

import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.LocalDateTime

@Entity
@Table(name = "task_comments")
data class TaskComment (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne
    @JoinColumn(name = "task_id",nullable = false)
    val task: Task,

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    val user: User,

    @Column(nullable = false)
    var commentText: String,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
){
}