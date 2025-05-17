package io.github.andreparkh

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
    fun ShouldCreateAndRetrieveUserViaService() {
        val createdUser = userService.createUser("test@example.com", "password", "testFirstName", "testLastName")
        val retrievedUser = userService.getUserById(createdUser.id!!)

        Assertions.assertNotNull(retrievedUser)
        Assertions.assertEquals("test@example.com", retrievedUser?.email)
    }

}