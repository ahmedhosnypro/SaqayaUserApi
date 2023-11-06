package com.saqaya.controller

import com.saqaya.dto.AuthenticationRequest
import com.saqaya.dto.UserRequest
import com.saqaya.dto.UserAccess
import com.saqaya.dto.UserResponse
import com.saqaya.model.User
import com.saqaya.service.AuthenticationService
import com.saqaya.service.UserService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService,
    private val authenticationService: AuthenticationService
) {
    @PostMapping
    @Transactional
    fun registerUser(@RequestBody userRequest: UserRequest): ResponseEntity<UserAccess> {
        val nUser: Pair<User, String> = userService.save(userRequest.toUser())
        val savedUser = nUser.first
        val password = nUser.second
        val id: String = savedUser.id!!
        val token = authenticationService.authenticate(savedUser.email, password)
        return ResponseEntity.ok(UserAccess(id, token))
    }

    @GetMapping("/{id}")
    fun getUser(
        @PathVariable id: String?,
        @RequestHeader("Authorization") accessToken: String?
    ): ResponseEntity<UserResponse> {
        val optUser = id?.let { userService.findById(it) }
        if (optUser != null && optUser.isPresent) {
            return ResponseEntity.ok(optUser.get().toUserDto())
        }
        return ResponseEntity.notFound().build()
    }

    @PostMapping("/login")
    fun login(@RequestBody @Valid request: AuthenticationRequest): ResponseEntity<String> {
        val token = authenticationService.authenticate(request.email, request.password)
        return ResponseEntity.ok(token)
    }
}