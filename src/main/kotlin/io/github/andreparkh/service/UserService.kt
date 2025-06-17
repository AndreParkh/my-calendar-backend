package io.github.andreparkh.service

import io.github.andreparkh.config.AppRoles
import io.github.andreparkh.dto.ResponseUser
import io.github.andreparkh.dto.UpdateUser
import io.github.andreparkh.model.User
import io.github.andreparkh.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UserService(private val userRepository: UserRepository) {

    fun createUser(
                    email: String,
                    passwordHash: String,
                    firstName: String,
                    lastName: String): ResponseUser {

        val user = User(
            email = email,
            passwordHash = passwordHash,
            firstName = firstName,
            lastName = lastName)

        val savedUser = userRepository.save(user)

        return savedUser.toResponseUser()
    }

    fun getUserById(id: Long): ResponseUser? {
        return userRepository.findById(id).orElse(null)?.toResponseUser()
    }

    fun updateUser(id: Long, updateUser: UpdateUser, currentUserEmail: String): ResponseUser? {

        val existingUser = userRepository.findById(id).orElse(null) ?: return null
        val currentUser = userRepository.findByEmail(currentUserEmail).orElse(null) ?: return null

        val isAdmin = currentUser.role == AppRoles.ADMIN_ROLE
        val isChangeSelf = currentUser.id == existingUser.id
        if (!isAdmin && !isChangeSelf) return null

        existingUser.firstName = updateUser.firstName
        existingUser.lastName = updateUser.lastName
        existingUser.avatarUrl = updateUser.avatarUrl
        existingUser.workStartTime = updateUser.workStartTime
        existingUser.workEndTime = updateUser.workEndTime
        existingUser.vacationStart = updateUser.vacationStart
        existingUser.vacationEnd = updateUser.vacationEnd
        existingUser.onUpdate()

        return userRepository.save(existingUser).toResponseUser()
    }

    fun getAllUsers(): List<ResponseUser> {
        return userRepository.findAll().map{ it.toResponseUser() }
    }

    fun deleteUserById(id: Long, currentUserEmail: String): Boolean {
        val existingUser = userRepository.findById(id).orElse(null) ?: return false
        val currentUser = userRepository.findByEmail(currentUserEmail).orElse(null) ?: return false

        val isAdmin = currentUser.role == AppRoles.ADMIN_ROLE
        val isDeleteSelf = currentUser.id == existingUser.id
        if (!isAdmin && !isDeleteSelf) return false

        userRepository.deleteById(id)
        return true
    }
}