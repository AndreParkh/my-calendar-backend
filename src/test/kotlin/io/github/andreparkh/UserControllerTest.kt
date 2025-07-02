package io.github.andreparkh

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.andreparkh.config.JwtConstants
import io.github.andreparkh.dto.auth.RegisterRequest
import io.github.andreparkh.dto.user.UpdateUser
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    val registerRequest1 = RegisterRequest(firstName = "John", lastName = "Doe", email = "john@example.com", password = "password")
    val registerRequest2 = RegisterRequest(firstName = "Jane", lastName = "Doe", email = "jane@example.com", password = "password")

    @Test
    fun `should get all users`() {

        registerUser(registerRequest1)
            .andExpect(status().isOk)

        val resultAction = registerUser(registerRequest2)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").isNotEmpty)

        val token = resultAction
            .andReturn()
            .response
            .contentAsString
            .let { objectMapper.readTree(it) }
            .get("token")
            .asText()

        mockMvc.perform(
            get("/api/private/users")
                .header(JwtConstants.AUTHORIZATION_HEADER, "${JwtConstants.TYPE_TOKEN} $token")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].firstName").value(registerRequest1.firstName))
            .andExpect(jsonPath("$[0].lastName").value(registerRequest1.lastName))
            .andExpect(jsonPath("$[1].firstName").value(registerRequest2.firstName))
            .andExpect(jsonPath("$[1].lastName").value(registerRequest2.lastName))
    }

    @Test
    fun `should get user by ID`() {

        val createResponse = registerUser(registerRequest1)
            .andExpect(status().isOk)
            .andReturn()

        val token = createResponse
            .response
            .contentAsString
            .let { objectMapper.readTree(it) }
            .get("token")
            .asText()

        val userList = mockMvc.perform(
            get("/api/private/users")
                .header(JwtConstants.AUTHORIZATION_HEADER, "${JwtConstants.TYPE_TOKEN} $token")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andReturn()
            .response
            .contentAsString
            .let { objectMapper.readTree( it ) }

        var userId: Long? = 0

        for (user in userList) {
            val email = user.get("email").asText()
            if (email == registerRequest1.email) {
                userId = user.get("id").asLong()
                break
            }
        }

        mockMvc.perform(
        get("/api/private/users/$userId")
            .header(JwtConstants.AUTHORIZATION_HEADER, "${JwtConstants.TYPE_TOKEN} $token")
            .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.firstName").value(registerRequest1.firstName))
            .andExpect(jsonPath("$.lastName").value(registerRequest1.lastName))
    }

    @Test
    fun `should return forbidden when getting user with invalid token`() {
        mockMvc.perform(
            get("/api/private/users")
                .header(JwtConstants.AUTHORIZATION_HEADER, "${JwtConstants.TYPE_TOKEN} InvalidToken")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isForbidden)
    }

    @Test
    fun `should update user`() {

        val createResponse = mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest1))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").isNotEmpty)
            .andDo { print( it ) }
            .andReturn()

        val token = createResponse
            .response
            .contentAsString
            .let { objectMapper.readTree(it) }
            .get("token")
            .asText()

        val userList = mockMvc.perform(
            get("/api/private/users")
                .header(JwtConstants.AUTHORIZATION_HEADER, "${JwtConstants.TYPE_TOKEN} $token")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andReturn()
            .response
            .contentAsString
            .let { objectMapper.readTree( it ) }

        var userId: Long? = 0

        for (user in userList) {
            val email = user.get("email").asText()
            if (email == registerRequest1.email) {
                userId = user.get("id").asLong()
                break
            }
        }

        val updateUser = UpdateUser(
            firstName = "JohnUpdate",
            lastName = "DoeUpdate",
            avatarUrl = "updated",
            workStartTime = LocalTime.of(9,0),
            workEndTime = LocalTime.of(18,0),
            vacationStart = LocalDate.of(2025, 1,1),
            vacationEnd = LocalDate.of(2025, 1, 14)
            )

        mockMvc.perform(
            put("/api/private/users/$userId")
                .header(JwtConstants.AUTHORIZATION_HEADER, "${JwtConstants.TYPE_TOKEN} $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUser))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.firstName").value(updateUser.firstName))
            .andExpect(jsonPath("$.lastName").value(updateUser.lastName))
            .andExpect(jsonPath("$.avatarUrl").value(updateUser.avatarUrl))
            .andExpect(jsonPath("$.workStartTime").value("09:00:00"))
            .andExpect(jsonPath("$.workEndTime").value("18:00:00"))
            .andExpect(jsonPath("$.vacationStart").value("2025-01-01"))
            .andExpect(jsonPath("$.vacationEnd").value("2025-01-14"))
    }


    @Test
    fun `should return not found when trying update an unregistering user`() {

        val createResponse = mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest1))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").isNotEmpty)
            .andReturn()

        val token = createResponse
            .response
            .contentAsString
            .let { objectMapper.readTree(it) }
            .get("token")
            .asText()

        val userList = mockMvc.perform(
            get("/api/private/users")
                .header(JwtConstants.AUTHORIZATION_HEADER, "${JwtConstants.TYPE_TOKEN} $token")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andReturn()
            .response
            .contentAsString
            .let { objectMapper.readTree( it ) }

        var userId: Long = 0

        for (user in userList) {
            val email = user.get("email").asText()
            if (email == registerRequest1.email) {
                userId = user.get("id").asLong()
                break
            }
        }

        val updateUser = UpdateUser(
            firstName = "JohnUpdate",
            lastName = "DoeUpdate",
            avatarUrl = "updated",
            workStartTime = LocalTime.of(9,0),
            workEndTime = LocalTime.of(18,0),
            vacationStart = LocalDate.of(2025, 1,1),
            vacationEnd = LocalDate.of(2025, 1, 14)
        )

        mockMvc.perform(
            put("/api/private/users/${userId + 1}")
                .header(JwtConstants.AUTHORIZATION_HEADER, "${JwtConstants.TYPE_TOKEN} $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUser))
        )
            .andExpect(status().isNotFound)
    }


    private fun registerUser(request: RegisterRequest): ResultActions {
        val createResponse = mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
        return createResponse
    }
}