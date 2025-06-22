package io.github.andreparkh.model

import io.github.andreparkh.dto.group.GroupMemberResponse
import io.github.andreparkh.enums.GroupMemberRole
import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.LocalDateTime


@Entity
@Table(name = "group_members")
data class GroupMember (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    var user: User,

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne
    @JoinColumn(name = "group_id",nullable = false)
    var group: Group,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: GroupMemberRole = GroupMemberRole.MEMBER,

    @Column(nullable = false)
    val jointedAt: LocalDateTime = LocalDateTime.now()
){
    fun toGroupMembersResponse() = GroupMemberResponse(
        userId = this.user.id!!,
        email = this.user.email,
        role = this.role,
        joinedAt = this.jointedAt
    )
}