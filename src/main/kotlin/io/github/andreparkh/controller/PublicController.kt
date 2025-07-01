package io.github.andreparkh.controller

import io.github.andreparkh.dto.user.UserResponse
import io.github.andreparkh.service.UserService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/public/")
@CrossOrigin(origins = ["http://localhost:5173"])
@Tag(name = "", description = "")
class PublicController(
    private val userService: UserService
) {
    @GetMapping("/user")
    fun mockUser(): ResponseEntity<UserResponse> {
        val user = userService.mockUser()
        return ResponseEntity.ok(user.toUserResponse())
    }
}