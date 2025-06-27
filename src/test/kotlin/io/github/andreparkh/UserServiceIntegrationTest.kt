package io.github.andreparkh

import io.github.andreparkh.dto.auth.RegisterRequest
import io.github.andreparkh.repository.UserRepository
import io.github.andreparkh.service.UserService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class UserServiceIntegrationTest {

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var userRepository: UserRepository

    @AfterEach
    fun tearDown() {
        userRepository.deleteAll()
    }

    @Test
    fun `Should create and retrieve user via service`() {
        val request = RegisterRequest(
            email = "test@example.com",
            password = "password",
            firstName = "testFirstName",
            lastName = "testLastName"
        )
        val createdUser = userService.createUser(request)
        val retrievedUser = userService.getUserById(createdUser.id)

        Assertions.assertNotNull(retrievedUser)
        Assertions.assertEquals(request.email, retrievedUser.email)
    }

}