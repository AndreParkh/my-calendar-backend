package io.github.andreparkh.service

import io.github.andreparkh.dto.UpdateUser
import io.github.andreparkh.model.User
import io.github.andreparkh.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {

    fun createUser(
                    email: String,
                    passwordHash: String,
                    firstName: String,
                    lastName: String): User {

        val user = User(
            email = email,
            passwordHash = passwordHash,
            firstName = firstName,
            lastName = lastName)
        return userRepository.save(user)
    }

    fun getUserById(id: Long): User? {
        return userRepository.findById(id).orElse(null)
    }

    fun updateUser(id: Long, updateUser: UpdateUser): User? {
        val existingUser = userRepository.findById(id).orElse(null)
        if(existingUser != null) {
            existingUser.firstName = updateUser.firstName
            existingUser.lastName = updateUser.lastName
            existingUser.avatarUrl = updateUser.avatarUrl
            existingUser.workStartTime = updateUser.workStartTime
            existingUser.workEndTime = updateUser.workEndTime
            existingUser.vacationStart = updateUser.vacationStart
            existingUser.vacationEnd = updateUser.vacationEnd

            return userRepository.save(existingUser)
        }
        return null
    }

    fun getAllUsers(): List<User> {
        return userRepository.findAll()
    }

    fun deleteUser(id: Long) {
        userRepository.deleteById(id)
    }

}