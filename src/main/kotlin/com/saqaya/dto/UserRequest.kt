package com.saqaya.dto

import com.saqaya.model.User
import jakarta.validation.constraints.NotBlank
import org.modelmapper.ModelMapper

data class UserRequest(
    @field:NotBlank var firstName: String? = null,
    @field:NotBlank var lastName: String? = null,
    @field:NotBlank var email: String? = null,
    var marketingConsent: Boolean = false,
){
    fun toUser(): User {
        val modelMapper = ModelMapper()
        return modelMapper.map(this, User::class.java)
    }
}