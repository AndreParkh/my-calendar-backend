package io.github.andreparkh

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.andreparkh.config.JwtConstants
import io.github.andreparkh.dto.auth.RegisterRequest
import io.github.andreparkh.dto.group.CreateGroupRequest
import io.github.andreparkh.dto.group.JoinGroupRequest
import org.junit.jupiter.api.BeforeEach
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
class GroupControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    val registerRequest1 = RegisterRequest(firstName = "John", lastName = "Doe", email = "john@example.com", password = "password")
    val registerRequest2 = RegisterRequest(firstName = "Jane", lastName = "Doe", email = "jane@example.com", password = "password")
    val createGroupRequest = CreateGroupRequest("Family")

    private var token1: String ?= null
    private var token2: String ?= null

    @BeforeEach
    fun setup() {

        val result1 = mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest1))
        ).andReturn()

        token1 = objectMapper.readTree(result1.response.contentAsString).get("token").asText()

        val result2 = mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest2))
        ).andReturn()

        token2 = objectMapper.readTree(result2.response.contentAsString).get("token").asText()
    }

    @Test
    fun `should create group`() {

        println(token1)
        println(token2)
        mockMvc.perform(
            post("/api/private/groups")
                .header(JwtConstants.AUTHORIZATION_HEADER, "${JwtConstants.TYPE_TOKEN} $token1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createGroupRequest))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value(createGroupRequest.name))
    }

    @Test
    fun `should get group by ID`() {
        // Шаг 1: создаем группу
        val result = mockMvc.perform(
            post("/api/private/groups")
                .header(JwtConstants.AUTHORIZATION_HEADER, "${JwtConstants.TYPE_TOKEN} $token1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createGroupRequest))
        )
            .andExpect(status().isCreated)
            .andReturn()

        val response = objectMapper.readTree(result.response.contentAsString)
        val groupId = response.get("id").asLong()

        // Шаг 2: получаем по ID
        mockMvc.perform(
            get("/api/private/groups/$groupId")
                .header(JwtConstants.AUTHORIZATION_HEADER, "${JwtConstants.TYPE_TOKEN} $token1")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value(createGroupRequest.name))
    }

    @Test
    fun `should join group`() {
        // Шаг 1: создаём группу
        val createdGroup = mockMvc.perform(
            post("/api/private/groups")
                .header(JwtConstants.AUTHORIZATION_HEADER, "${JwtConstants.TYPE_TOKEN} $token1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createGroupRequest))
        )
            .andExpect(status().isCreated)
            .andReturn()

        val createdGroupResponse = objectMapper.readTree(createdGroup.response.contentAsString)
        val inviteToken = createdGroupResponse.get("inviteToken").asText()
        val joinGroupRequest = JoinGroupRequest(inviteToken)

        // Шаг 2: присоединяемся
        mockMvc.perform(
            post("/api/private/groups/join")
                .header(JwtConstants.AUTHORIZATION_HEADER, "${JwtConstants.TYPE_TOKEN} $token2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(joinGroupRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value(createGroupRequest.name))
    }

    @Test
    fun `should get group members list`() {
        // Шаг 1: создаём группу
        val createResult = mockMvc.perform(
            post("/api/private/groups")
                .header(JwtConstants.AUTHORIZATION_HEADER, "${JwtConstants.TYPE_TOKEN} $token1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createGroupRequest))
        )
            .andExpect(status().isCreated)
            .andReturn()

        val createResponse = objectMapper.readTree(createResult.response.contentAsString)
        val groupId = createResponse.get("id").asLong()
        val inviteToken = createResponse.get("inviteToken").asText()
        val joinGroupRequest = JoinGroupRequest(inviteToken)

        // Шаг 2: присоединяем второго пользователя
        mockMvc.perform(
            post("/api/private/groups/join")
                .header(JwtConstants.AUTHORIZATION_HEADER, "${JwtConstants.TYPE_TOKEN} $token2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(joinGroupRequest))
        )
            .andExpect(status().isOk)

        // Шаг 3: получаем список участников
        mockMvc.perform(
            get("/api/private/groups/$groupId/members")
                .header(JwtConstants.AUTHORIZATION_HEADER, "${JwtConstants.TYPE_TOKEN} $token1")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].email").value(registerRequest1.email))
            .andExpect(jsonPath("$[1].email").value(registerRequest2.email))
    }
}