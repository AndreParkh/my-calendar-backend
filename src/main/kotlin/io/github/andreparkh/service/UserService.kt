package io.github.andreparkh.service

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

    fun updateUser(email: String, updateUser: UpdateUser): ResponseUser? {

        val existingUser = userRepository.findByEmail(email).orElse(null) ?: return null

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

    fun deleteUserById(id: Long): Boolean {
        if (!userRepository.existsById(id)) return false
        userRepository.deleteById(id)
        return true
    }

    fun deleteUserByEmail(email: String): Boolean {
        val deletedUser = userRepository.findByEmail(email).orElse(null) ?: return false
        deletedUser.onDelete()
        return true
    }

}