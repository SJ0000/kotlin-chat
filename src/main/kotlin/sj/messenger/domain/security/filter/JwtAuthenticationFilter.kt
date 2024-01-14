package sj.messenger.domain.security.filter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.kotlinModule
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import sj.messenger.domain.security.authentication.AuthenticatedUser
import sj.messenger.domain.security.authentication.GuestUser
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
        // 토큰이 없으면 게스트, 있으면 로그인 사용자
        request.getHeader(HttpHeaders.AUTHORIZATION)?.let { header ->
            {
                val token = validateAndParseToken(header)
                val userId = jwtParser.validateAndGetUserId(token)

            }

        } ?: run {
            SecurityContextHolder.getContext().authentication = GuestUser()
        }


    }

    private fun validateAndParseToken(header: String): String {
        if (header.startsWith("Bearer "))
            throw RuntimeException("invalid authorization type.")
        return header.substring(7)
    }
}