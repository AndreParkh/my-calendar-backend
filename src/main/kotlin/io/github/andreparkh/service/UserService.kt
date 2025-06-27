package io.github.andreparkh.service

import io.github.andreparkh.config.AppRoles
import io.github.andreparkh.dto.auth.RegisterRequest
import io.github.andreparkh.dto.user.UserResponse
import io.github.andreparkh.dto.user.UpdateUser
import io.github.andreparkh.model.User
import io.github.andreparkh.repository.UserRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    ) {

    fun createUser(request: RegisterRequest): UserResponse {
        if (userRepository.existsByEmail(request.email))
            throw IllegalArgumentException("Пользователь с таким email уже существует")

        val user = User(
            firstName = request.firstName,
            lastName =  request.lastName,
            email = request.email,
            passwordHash = passwordEncoder.encode(request.password),
        )

        val savedUser = userRepository.save(user)
        userRepository.flush()

        return savedUser.toUserResponse()
    }

    fun getUserById(id: Long): UserResponse {
        val foundUser = userRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Пользователь с ID $id не найден") }
        return foundUser.toUserResponse()
    }

    fun getAllUsers(): List<UserResponse> {
        return userRepository.findAll().map{ it.toUserResponse() }
    }

    fun updateUser(id: Long, updateUser: UpdateUser): UserResponse {
        val existingUser = userRepository.findById(id)
            .orElseThrow{ EntityNotFoundException("Пользователь с ID $id не найден") }

        val currentUser = getCurrentUser()

        val isChangeSelf = currentUser.id == existingUser.id
        if (!currentUser.isAdmin() && !isChangeSelf)
            throw AccessDeniedException("Недостаточно прав для изменения")

        existingUser.firstName = updateUser.firstName
        existingUser.lastName = updateUser.lastName
        existingUser.avatarUrl = updateUser.avatarUrl
        existingUser.workStartTime = updateUser.workStartTime
        existingUser.workEndTime = updateUser.workEndTime
        existingUser.vacationStart = updateUser.vacationStart
        existingUser.vacationEnd = updateUser.vacationEnd
        existingUser.onUpdate()

        return userRepository.save(existingUser).toUserResponse()
    }

    fun changeRoleById(id: Long, newRole: String): Boolean {
        if (newRole !in listOf(AppRoles.USER_ROLE, AppRoles.ADMIN_ROLE))
            throw IllegalArgumentException("Неккоректная роль: $newRole")

        val existingUser = userRepository.findById(id)
            .orElseThrow{ EntityNotFoundException("Пользователь с ID $id не найден") }

        val currentUser = getCurrentUser()

        if (!currentUser.isAdmin())
            throw AccessDeniedException("Недостаточно прав для изменения")

        existingUser.role = newRole
        userRepository.save(existingUser)
        return true
    }

    fun deleteUserById(id: Long): Boolean {
        val existingUser = userRepository.findById(id)
            .orElseThrow{ EntityNotFoundException("Пользователь с ID $id не найден") }

        val currentUser = getCurrentUser()
        val isDeleteSelf = currentUser.id == existingUser.id

        if (!currentUser.isAdmin() && !isDeleteSelf)
            throw AccessDeniedException("Недостаточно прав для удаления")

        userRepository.deleteById(id)
        return true
    }

    fun getCurrentUser(): User {

        val authUserEmail = SecurityContextHolder.getContext().authentication.name
        val currentUser = userRepository.findByEmail(authUserEmail)
            .orElseThrow{ EntityNotFoundException("Текущий пользователь не найден") }

        return currentUser
    }

}