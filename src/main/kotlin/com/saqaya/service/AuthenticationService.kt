package com.saqaya.service

import com.saqaya.exception.AuthenticationFailedException
import com.saqaya.model.User
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;


@Service
class AuthenticationService(
    private val authenticationManager: AuthenticationManager,
    private val tokenService: TokenService
) {
    fun authenticate(email: String?, password: String?): String {
        return try {
            // The authentication manager provides secure authentication and throws exception if it fails
            val authToken = UsernamePasswordAuthenticationToken(email, password)
            val authenticate: Authentication = authenticationManager.authenticate(authToken)
            val user: User = authenticate.principal as User
            tokenService.generateToken(user)
        } catch (e: AuthenticationException) {
            throw AuthenticationFailedException("Invalid User or Password")
        }
    }
}