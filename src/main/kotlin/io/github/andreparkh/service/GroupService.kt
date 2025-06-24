package io.github.andreparkh.service

import io.github.andreparkh.dto.group.CreateGroupRequest
import io.github.andreparkh.dto.group.GroupMemberResponse
import io.github.andreparkh.dto.group.GroupResponse
import io.github.andreparkh.enums.GroupMemberRole
import io.github.andreparkh.model.Group
import io.github.andreparkh.model.GroupMember
import io.github.andreparkh.repository.GroupMemberRepository
import io.github.andreparkh.repository.GroupRepository
import io.github.andreparkh.repository.UserRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service

@Service
class GroupService(
    private val groupRepository: GroupRepository,
    private val groupMemberRepository: GroupMemberRepository,
    private val userRepository: UserRepository
) {
    fun createGroup(createGroupRequest: CreateGroupRequest, currentUserEmail: String): GroupResponse {

        val currentUser = userRepository.findByEmail(currentUserEmail)
            .orElseThrow{ EntityNotFoundException("Текущий пользователь не найден") }

        val group = Group(
            name = createGroupRequest.name,
        )

        val adminMember = GroupMember(
            user = currentUser,
            group = group,
            role = GroupMemberRole.ADMIN
        )

        val savedGroup = groupRepository.save(group)
        groupMemberRepository.save(adminMember)

        return savedGroup.toGroupResponse()
    }

    fun getGroupById(groupId: Long): GroupResponse {
        val group = groupRepository.findById(groupId)
            .orElseThrow { EntityNotFoundException("Группа с ID $groupId не найдена") }
        return group.toGroupResponse()
    }

    fun joinGroup(inviteToken: String, currentUserEmail: String): GroupResponse {
        val group = groupRepository.findByInviteToken(inviteToken)
            .orElseThrow { EntityNotFoundException("Группа с таким токеном не найдена") }

        val currentUser = userRepository.findByEmail(currentUserEmail)
            .orElseThrow{ EntityNotFoundException("Текущий пользователь не найден") }

        if (!group.isTokenValid()) throw IllegalArgumentException("Токен недействитен")

        if (groupMemberRepository.existsByUserIdAndGroupId(currentUser.id!!, group.id!!))
            throw IllegalArgumentException("Пользователь уже состоит в группе")

        val member = GroupMember(
            user = currentUser,
            group = group,
        )

        groupMemberRepository.save(member)

        return group.toGroupResponse()
    }

    fun getAllGroupMembersByGroupId(groupId: Long): List<GroupMemberResponse> {

        getGroupById(groupId)
        val members = groupMemberRepository.findByGroupId(groupId)
        return members.map { member -> member.toGroupMembersResponse() }
    }
}