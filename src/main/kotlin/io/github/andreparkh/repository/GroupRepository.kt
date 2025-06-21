package io.github.andreparkh.repository

import io.github.andreparkh.model.Group
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface GroupRepository: JpaRepository<Group, Long> {
    fun findByInviteToken(inviteToken: String): Optional<Group>
}