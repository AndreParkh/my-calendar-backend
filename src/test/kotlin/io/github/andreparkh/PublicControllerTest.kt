package io.github.andreparkh

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PublicControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `should get mock public user`() {
        mockMvc.perform(
            get("/api/public/user")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(999))
            .andExpect(jsonPath("$.firstName").value("Иван"))
            .andExpect(jsonPath("$.lastName").value("Иванов"))
            .andExpect(jsonPath("$.email").value("Ivanov@example.com"))
            .andExpect(jsonPath("$.workStartTime").value("09:00:00"))
            .andExpect(jsonPath("$.workEndTime").value("18:00:00"))
            .andExpect(jsonPath("$.vacationStart").value("2025-01-01"))
            .andExpect(jsonPath("$.vacationEnd").value("2025-01-14"))
            .andExpect(jsonPath("$.role").value("USER_ROLE"))
    }
}