package com.saqaya.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty


class AuthenticationRequest {
    @NotEmpty(message = "{required.field}")
    @Email(message = "{invalid.field}")
    val email: String? = null

    @NotEmpty(message = "{required.field}")
    val password: String? = null
}