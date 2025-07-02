package io.github.andreparkh.service

import io.github.andreparkh.config.GroupErrorMessages
import io.github.andreparkh.dto.group.CreateGroupRequest
import io.github.andreparkh.dto.group.GroupMemberResponse
import io.github.andreparkh.dto.group.GroupResponse
import io.github.andreparkh.enums.GroupMemberRole
import io.github.andreparkh.model.Group
import io.github.andreparkh.model.GroupMember
import io.github.andreparkh.repository.GroupMemberRepository
import io.github.andreparkh.repository.GroupRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service

@Service
class GroupService(
    private val groupRepository: GroupRepository,
    private val groupMemberRepository: GroupMemberRepository,
    private val userService: UserService
) {
    fun createGroup(createGroupRequest: CreateGroupRequest): GroupResponse {

        val currentUser = userService.getCurrentUser()

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
            .orElseThrow { EntityNotFoundException(String.format(GroupErrorMessages.NOT_FOUND_BY_ID, groupId))}
        return group.toGroupResponse()
    }

    fun joinGroup(inviteToken: String): GroupResponse {
        val group = groupRepository.findByInviteToken(inviteToken)
            .orElseThrow { EntityNotFoundException(GroupErrorMessages.NOT_FOUND_BY_TOKEN) }

        val currentUser = userService.getCurrentUser()

        if (!group.isTokenValid()) throw IllegalArgumentException(GroupErrorMessages.TOKEN_INVALID)

        if (groupMemberRepository.existsByUserIdAndGroupId(currentUser.getId(), group.getId()))
            throw IllegalArgumentException(GroupErrorMessages.USER_ALREADY_IN_GROUP)

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