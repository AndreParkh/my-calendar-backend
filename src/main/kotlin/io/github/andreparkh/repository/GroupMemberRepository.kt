package io.github.andreparkh.repository

import io.github.andreparkh.model.GroupMember
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GroupMemberRepository: JpaRepository<GroupMember, Long> {
    fun existsByUserIdAndGroupId(userId: Long, groupId: Long): Boolean
    fun findByGroupId(groupId: Long): List<GroupMember>
}