package sj.messenger.domain.security.filter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import sj.messenger.domain.security.authentication.AuthenticationToken
import sj.messenger.domain.security.authentication.principal.GuestUserDetails
import sj.messenger.domain.security.authentication.principal.LoginUserDetails
import sj.messenger.domain.security.jwt.JwtParser


class JwtAuthenticationFilter(
    private val jwtParser: JwtParser
) : OncePerRequestFilter() {

    private val objectMapper = ObjectMapper().registerModules(kotlinModule())

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val header = request.getHeader(HttpHeaders.AUTHORIZATION)
            val token = validateAndParseToken(header)
            val userClaim = jwtParser.validateAndGetUserClaim(token)
            SecurityContextHolder.getContext().authentication = AuthenticationToken(LoginUserDetails(userClaim))
            println("authentication success")
        } catch (e: Exception) {
            println("error = ${e}")
            SecurityContextHolder.getContext().authentication = AuthenticationToken(GuestUserDetails())
        }finally {
            filterChain.doFilter(request,response)
        }
    }

    private fun validateAndParseToken(header: String): String {
        if (header.startsWith("Bearer "))
            throw RuntimeException("invalid authorization type.")
        return header.substring(7)
    }
}