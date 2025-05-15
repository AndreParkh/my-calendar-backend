package io.github.andreparkh.model

import io.github.andreparkh.enums.GroupMemberRole
import jakarta.persistence.*
import java.time.LocalDateTime


@Entity
@Table(name = "group_members")
class GroupMember (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    var user: User,

    @ManyToOne
    @JoinColumn(name = "group_id",nullable = false)
    var group: Group,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: GroupMemberRole? = GroupMemberRole.MEMBER,

    @Column(nullable = false)
    val jointedAt: LocalDateTime = LocalDateTime.now()
){
}