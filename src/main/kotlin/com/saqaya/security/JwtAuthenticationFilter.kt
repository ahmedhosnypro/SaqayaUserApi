package com.saqaya.security

import com.auth0.jwt.exceptions.JWTVerificationException
import com.fasterxml.jackson.databind.ObjectMapper
import com.saqaya.service.TokenService
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException


@Component
class JwtAuthenticationFilter : OncePerRequestFilter() {
    private val tokenService: TokenService? = null
    private val userService: UserDetailsService? = null

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }
        try {
            val token: String = tokenService?.getTokenFrom(authorizationHeader)
                ?: throw JWTVerificationException("Invalid Authorization Header")
            val userEmail: String = tokenService.getSubjectFrom(token)
            val user = userService!!.loadUserByUsername(userEmail)
            val authenticationToken = UsernamePasswordAuthenticationToken(userEmail, null, user.authorities)
            authenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)
            SecurityContextHolder.getContext().authentication = authenticationToken
            filterChain.doFilter(request, response)
        } catch (ex: JWTVerificationException) {
            ex.printStackTrace() // log error
            response.setHeader("error", ex.message)

            response.status = HttpStatus.FORBIDDEN.value()
            val error: MutableMap<String, String?> = HashMap()
            error["error"] = ex.message
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            ObjectMapper().writeValue(response.outputStream, error)
        }
    }
}