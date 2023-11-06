package com.saqaya.service

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import com.saqaya.model.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Service
class TokenService {
    @Value("\${secret.key}")
    private val secret: String? = null

    fun getTokenFrom(bearerToken: String?): String {
        val bearer = "Bearer "
        if (bearerToken == null || !bearerToken.startsWith(bearer)) throw JWTVerificationException("Invalid Authorization Header")
        return bearerToken.substring(bearer.length)
    }

    fun getSubjectFrom(token: String?): String {
        val algorithm: Algorithm = Algorithm.HMAC256(secret!!.toByteArray())
        val verifier: JWTVerifier = JWT.require(algorithm).build()
        val decodedJWT: DecodedJWT = verifier.verify(token) // throws JWTVerificationException if not valid
        return decodedJWT.subject
    }

    fun generateToken(user: User): String {
        val algorithm: Algorithm = Algorithm.HMAC256(secret!!.toByteArray())
        val expiration = generateExpirationTimeIn(60) // expires in 60 minutes
        return JWT.create()
            .withSubject(user.getUsername())
            .withExpiresAt(expiration)
            .withIssuer("Books-API")
            .withClaim("roles", user.role.name)
            .sign(algorithm)
    }

    private fun generateExpirationTimeIn(minutes: Int): Instant {
        return LocalDateTime.now().plusMinutes(minutes.toLong()).atZone(ZoneId.systemDefault()).toInstant()
    }
}