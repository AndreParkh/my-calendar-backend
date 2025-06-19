package io.github.andreparkh.service

import io.github.andreparkh.config.AppRoles
import io.github.andreparkh.dto.ResponseUser
import io.github.andreparkh.dto.UpdateUser
import io.github.andreparkh.model.User
import io.github.andreparkh.repository.UserRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service

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

        val existingUser = userRepository.findById(id)
            .orElseThrow{ EntityNotFoundException("Пользователь с ID $id не найден") }

        val currentUser = userRepository.findByEmail(currentUserEmail)
            .orElseThrow{ EntityNotFoundException("Текущий пользователь не найден") }

        val isAdmin = currentUser.role == AppRoles.ADMIN_ROLE
        val isChangeSelf = currentUser.id == existingUser.id
        if (!isAdmin && !isChangeSelf)
            throw AccessDeniedException("Недостаточно прав для изменения")

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
        val existingUser = userRepository.findById(id)
            .orElseThrow{ EntityNotFoundException("Пользователь с ID $id не найден") }

        val currentUser = userRepository.findByEmail(currentUserEmail)
            .orElseThrow{ EntityNotFoundException("Текущий пользователь не найден") }

        val isAdmin = currentUser.isAdmin()
        val isDeleteSelf = currentUser.id == existingUser.id

        if (!isAdmin && !isDeleteSelf)
            throw AccessDeniedException("Недостаточно прав для изменения")

        userRepository.deleteById(id)
        return true
    }

    fun changeRoleById(id: Long, newRole: String, currentUserEmail: String): Boolean {
        if (newRole !in listOf(AppRoles.USER_ROLE, AppRoles.ADMIN_ROLE))
            throw IllegalArgumentException("Неккоректная роль: $newRole")

        val existingUser = userRepository.findById(id)
            .orElseThrow{ EntityNotFoundException("Пользователь с ID $id не найден") }

        val currentUser = userRepository.findByEmail(currentUserEmail)
            .orElseThrow{ EntityNotFoundException("Текущий пользователь не найден") }

        if (!currentUser.isAdmin())
            throw AccessDeniedException("Недостаточно прав для изменения")

        existingUser.role = newRole
        userRepository.save(existingUser)
        return true
    }

}