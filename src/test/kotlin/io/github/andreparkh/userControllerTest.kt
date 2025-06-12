package io.github.andreparkh

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.andreparkh.model.User
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class userControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `should get all users`() {
        val user1 = User(firstName = "John", lastName = "Doe", email = "john@example.com", passwordHash = "123")
        val user2 = User(firstName = "Jane", lastName = "Doe", email = "jane@example.com", passwordHash = "123")

        mockMvc.perform(
            post("/private/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user1))
        ).andExpect(status().isCreated)

        mockMvc.perform(
            post("/private/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user2))
        ).andExpect(status().isCreated)

        mockMvc.perform(get("/private/users"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].firstName").value("John"))
            .andExpect(jsonPath("$[1].lastName").value("Doe"))
    }

    @Test
    fun `should update user`() {
        val createUser = User(firstName = "John", lastName = "Doe", email = "john@example.com", passwordHash = "123")
        val createResponse = mockMvc.perform(
            post("/private/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUser))
        ).andExpect(status().isCreated).andReturn()

        val userId = objectMapper.readTree(createResponse.response.contentAsString).get("id").asLong()

        val updateUser = User(firstName = "JohnUpdate", lastName = "DoeUpdate", email = "john@example.com", passwordHash = "123")

        mockMvc.perform(
            put("/private/users/$userId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUser))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.firstName").value("JohnUpdated"))
            .andExpect(jsonPath("$.lastName").value("DoeUpdated"))

    }
}