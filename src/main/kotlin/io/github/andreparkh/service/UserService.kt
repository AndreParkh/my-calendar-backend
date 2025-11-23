package io.github.andreparkh.service

import io.github.andreparkh.config.AppRoles
import io.github.andreparkh.config.UserErrorMessages
import io.github.andreparkh.dto.auth.RegisterRequest
import io.github.andreparkh.dto.user.UserResponse
import io.github.andreparkh.dto.user.UpdateUser
import io.github.andreparkh.model.User
import io.github.andreparkh.repository.UserRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalTime

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    ) {

    fun createUser(request: RegisterRequest): UserResponse {
        if (userRepository.existsByEmail(request.email))
            throw IllegalArgumentException(UserErrorMessages.EMAIL_ALREADY_EXISTS)

        val user = User(
            firstName = request.firstName,
            lastName =  request.lastName,
            email = request.email,
            passwordHash = passwordEncoder.encode(request.password),
            avatarUrl = getDefaultAvatarUrl(request.firstName, request.lastName)
        )

        val savedUser = userRepository.save(user)
        userRepository.flush()

        return savedUser.toUserResponse()
    }

    fun getUserById(id: Long): UserResponse {
        val foundUser = userRepository.findById(id)
            .orElseThrow { EntityNotFoundException(String.format(UserErrorMessages.NOT_FOUND_BY_ID))}
        return foundUser.toUserResponse()
    }

    fun getAllUsers(): List<UserResponse> {
        return userRepository.findAll().map{ it.toUserResponse() }
    }

    fun updateUser(id: Long, updateUser: UpdateUser): UserResponse {
        val existingUser = userRepository.findById(id)
            .orElseThrow{ EntityNotFoundException(String.format(UserErrorMessages.NOT_FOUND_BY_ID, id))}

        val currentUser = getCurrentUser()

        val isChangeSelf = currentUser.getId() == existingUser.getId()
        if (!currentUser.isAdmin() && !isChangeSelf)
            throw AccessDeniedException(UserErrorMessages.ACCESS_DENIED)

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
            throw IllegalArgumentException(String.format(UserErrorMessages.INVALID_ROLE, newRole))

        val existingUser = userRepository.findById(id)
            .orElseThrow{ EntityNotFoundException(String.format(UserErrorMessages.NOT_FOUND_BY_ID, id))}

        val currentUser = getCurrentUser()

        if (!currentUser.isAdmin())
            throw AccessDeniedException(UserErrorMessages.ACCESS_DENIED)

        existingUser.role = newRole
        userRepository.save(existingUser)
        return true
    }

    fun deleteUserById(id: Long): Boolean {
        val existingUser = userRepository.findById(id)
            .orElseThrow{ EntityNotFoundException(String.format(UserErrorMessages.NOT_FOUND_BY_ID, id))}

        val currentUser = getCurrentUser()
        val isDeleteSelf = currentUser.getId() == existingUser.getId()

        if (!currentUser.isAdmin() && !isDeleteSelf)
            throw AccessDeniedException(UserErrorMessages.ACCESS_DENIED)

        userRepository.deleteById(id)
        return true
    }

    fun mockUser(): User {
        return User(
            id = 999,
            firstName = "Иван",
            lastName =  "Иванов",
            email = "Ivanov@example.com",
            passwordHash = passwordEncoder.encode("test"),
            vacationStart = LocalDate.of(2025,1,1),
            vacationEnd = LocalDate.of(2025, 1,14),
            workStartTime = LocalTime.of(9,0),
            workEndTime = LocalTime.of(18 ,0),
        )
    }

    fun getDefaultAvatarUrl(firstName: String, lastName: String): String {
        return "https://avatar.iran.liara.run/username?username=$firstName+$lastName"
    }

    fun getCurrentUser(): User {

        val authUserEmail = SecurityContextHolder.getContext().authentication.name
        val currentUser = userRepository.findByEmail(authUserEmail)
            .orElseThrow{ EntityNotFoundException(String.format(UserErrorMessages.NOT_FOUND_BY_EMAIL, authUserEmail))}

        return currentUser
    }

}