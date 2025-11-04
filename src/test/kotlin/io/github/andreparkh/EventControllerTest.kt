package io.github.andreparkh

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.andreparkh.config.EventParticipantStatus
import io.github.andreparkh.config.JwtConstants
import io.github.andreparkh.dto.auth.RegisterRequest
import io.github.andreparkh.dto.event.EventRequest
import io.github.andreparkh.dto.event.JoinEventRequest
import io.github.andreparkh.dto.event.UpdateParticipationStatusRequest
import org.junit.jupiter.api.BeforeEach
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
import java.time.LocalDateTime

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EventControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    val registerRequest1 = RegisterRequest(firstName = "John", lastName = "Doe", email = "john@example.com", password = "password")
    val registerRequest2 = RegisterRequest(firstName = "Jane", lastName = "Doe", email = "jane@example.com", password = "password")

    val eventRequest = EventRequest(
        title = "Team Meeting",
        description = "Weekly team sync",
        startTime = LocalDateTime.now().plusDays(1),
        endTime = LocalDateTime.now().plusHours(1)
    )

    val eventRequest2 = EventRequest(
        title = "Team Meeting",
        description = "Weekly team sync",
        startTime = LocalDateTime.now().plusDays(1),
        endTime = LocalDateTime.now().plusDays(1).plusHours(1)
    )

    private var token1: String? = null
    private var token2: String? = null
    private var userId1: Long? = null
    private var userId2: Long? = null

    @BeforeEach
    fun setup() {
        // Регистрация пользователя 2
        val result1 = mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest1))
        ).andReturn()

        token1 = objectMapper.readTree(result1.response.contentAsString).get("token").asText()

        // Регистрация пользователя 2
        val result2 = mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest2))
        ).andReturn()

        token2 = objectMapper.readTree(result2.response.contentAsString).get("token").asText()
    }

    @Test
    fun `should create event`() {
        createEvent(token1!!, eventRequest)
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.title").value(eventRequest.title))
            .andExpect(jsonPath("$.description").value(eventRequest.description))
    }

    @Test
    fun `should return forbidden when creating event with invalid token`() {
        createEvent("InvalidToken", eventRequest)
            .andExpect(status().isForbidden)
    }

    @Test
    fun `should get event by ID`() {

        val createdEvent = createEvent(token1!!, eventRequest)
            .andExpect(status().isCreated)
            .andReturn()

        val eventId = objectMapper
            .readTree(createdEvent.response.contentAsString)
            .get("id")
            .asLong()

        mockMvc.perform(
            get("/api/private/events/$eventId")
                .header(JwtConstants.AUTHORIZATION_HEADER, "${JwtConstants.TYPE_TOKEN} $token1")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(eventId))
            .andExpect(jsonPath("$.title").value(eventRequest.title))
    }

    @Test
    fun `should return forbidden when trying to get event by another user`() {

        val createdEvent = createEvent(token1!!, eventRequest)
            .andExpect(status().isCreated)
            .andReturn()

        val eventId = objectMapper
            .readTree(createdEvent.response.contentAsString)
            .get("id")
            .asLong()

        mockMvc.perform(
            get("/api/private/events/$eventId")
                .header(JwtConstants.AUTHORIZATION_HEADER, "${JwtConstants.TYPE_TOKEN} $token2")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isForbidden)
    }

    @Test
    fun `should return not found when trying to get event by non-exiting ID`() {

        val createdEvent = createEvent(token1!!, eventRequest)
            .andExpect(status().isCreated)
            .andReturn()

        val eventId = objectMapper
            .readTree(createdEvent.response.contentAsString)
            .get("id")
            .asLong()

        mockMvc.perform(
            get("/api/private/events/${eventId + 1}")
                .header(JwtConstants.AUTHORIZATION_HEADER, "${JwtConstants.TYPE_TOKEN} $token1")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should join event`() {
        val createdEvent = createEvent(token1!!, eventRequest)
            .andExpect(status().isCreated)
            .andReturn()

        val eventId = objectMapper
            .readTree(createdEvent.response.contentAsString)
            .get("id")
            .asLong()

        val responseUsers = mockMvc.perform(
            get("/api/private/users")
                .header(JwtConstants.AUTHORIZATION_HEADER, "${JwtConstants.TYPE_TOKEN} $token1")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn()

        val users = objectMapper.readTree(responseUsers.response.contentAsString)
        userId1 = users[0].get("id").asLong()
        userId2 = users[1].get("id").asLong()

        val joinRequest = JoinEventRequest(
            userId = userId2!!
        )

        joinUserToEvent(token1!!, eventId, joinRequest)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].userId").value(userId1))
            .andExpect(jsonPath("$[1].userId").value(userId2))
    }

    @Test
    fun `should return bad request when trying to join own event`() {
        val createdEvent = createEvent(token1!!, eventRequest)
            .andExpect(status().isCreated)
            .andReturn()

        val eventId = objectMapper
            .readTree(createdEvent.response.contentAsString)
            .get("id")
            .asLong()

        val responseUsers = mockMvc.perform(
            get("/api/private/users")
                .header(JwtConstants.AUTHORIZATION_HEADER, "${JwtConstants.TYPE_TOKEN} $token1")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn()

        val users = objectMapper.readTree(responseUsers.response.contentAsString)
        userId1 = users[0].get("id").asLong()

        val joinRequest = JoinEventRequest(
            userId = userId1!!
        )

        joinUserToEvent(token1!!, eventId, joinRequest)
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should update status`() {
        val createdEvent = createEvent(token1!!, eventRequest)
            .andExpect(status().isCreated)
            .andReturn()

        val eventId = objectMapper
            .readTree(createdEvent.response.contentAsString)
            .get("id")
            .asLong()

        val responseUsers = mockMvc.perform(
            get("/api/private/users")
                .header(JwtConstants.AUTHORIZATION_HEADER, "${JwtConstants.TYPE_TOKEN} $token1")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn()

        val users = objectMapper.readTree(responseUsers.response.contentAsString)
        userId1 = users[0].get("id").asLong()
        userId2 = users[1].get("id").asLong()

        val joinRequest = JoinEventRequest(
            userId = userId2!!
        )
        joinUserToEvent(token1!!, eventId, joinRequest)
            .andExpect(status().isOk)

        val updateStatusRequest = UpdateParticipationStatusRequest(
            participantId = userId2!!,
            newStatus = EventParticipantStatus.ACCEPTED
        )

        mockMvc.perform(
            put("/api/private/events/$eventId/update-status")
                .header(JwtConstants.AUTHORIZATION_HEADER, "${JwtConstants.TYPE_TOKEN} $token2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateStatusRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.userId").value(userId2))
            .andExpect(jsonPath("$.status").value(updateStatusRequest.newStatus))
    }

    @Test
    fun `should return bad request when updating status with invalid status`() {
        val createdEvent = createEvent(token1!!, eventRequest)
            .andExpect(status().isCreated)
            .andReturn()

        val eventId = objectMapper
            .readTree(createdEvent.response.contentAsString)
            .get("id")
            .asLong()

        val responseUsers = mockMvc.perform(
            get("/api/private/users")
                .header(JwtConstants.AUTHORIZATION_HEADER, "${JwtConstants.TYPE_TOKEN} $token1")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn()

        val users = objectMapper.readTree(responseUsers.response.contentAsString)
        userId1 = users[0].get("id").asLong()
        userId2 = users[1].get("id").asLong()

        val joinRequest = JoinEventRequest(
            userId = userId2!!
        )
        joinUserToEvent(token1!!, eventId, joinRequest)
            .andExpect(status().isOk)

        val updateStatusRequest = UpdateParticipationStatusRequest(
            participantId = userId2!!,
            newStatus = "InvalidStatus"
        )

        mockMvc.perform(
            put("/api/private/events/$eventId/update-status")
                .header(JwtConstants.AUTHORIZATION_HEADER, "${JwtConstants.TYPE_TOKEN} $token2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateStatusRequest))
        )
            .andExpect(status().isBadRequest)
    }


    @Test
    fun `should get event by participants and date `() {

        val createdEvent1 = createEvent(token1!!, eventRequest)
            .andExpect(status().isCreated)
            .andReturn()

        val createdEvent2 = createEvent(token1!!, eventRequest2)
            .andExpect(status().isCreated)
            .andReturn()

        val eventId1 = objectMapper
            .readTree(createdEvent1.response.contentAsString)
            .get("id")
            .asLong()

        val eventId2 = objectMapper
            .readTree(createdEvent2.response.contentAsString)
            .get("id")
            .asLong()

        mockMvc.perform(
            get("/api/private/events/date-between")
                .header(JwtConstants.AUTHORIZATION_HEADER, "${JwtConstants.TYPE_TOKEN} $token1")
                .param("start", LocalDateTime.now().minusDays(1).toString())
                .param("end", LocalDateTime.now().plusDays(2).toString())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(eventId1))
            .andExpect(jsonPath("$[1].id").value(eventId2))
    }


    private fun createEvent(authToken: String, eventRequest: EventRequest): ResultActions {
        val createEventResponse = mockMvc.perform(
            post("/api/private/events")
                .header(JwtConstants.AUTHORIZATION_HEADER, "${JwtConstants.TYPE_TOKEN} $authToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventRequest))
        )
        return createEventResponse
    }

    private fun joinUserToEvent(authToken: String,eventId: Long ,joinRequest: JoinEventRequest): ResultActions {
        val participantResponse = mockMvc.perform(
            post("/api/private/events/$eventId/join")
                .header(JwtConstants.AUTHORIZATION_HEADER, "${JwtConstants.TYPE_TOKEN} $authToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(joinRequest))
        )
        return participantResponse
    }

}
