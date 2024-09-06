package sj.messenger.domain.security.filter

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import sj.messenger.domain.security.authentication.AuthenticatedToken
import sj.messenger.domain.security.authentication.JwtPreAuthenticationToken
import sj.messenger.domain.security.authentication.principal.GuestUserDetails
import sj.messenger.global.dto.ErrorResponse
import sj.messenger.global.exception.ErrorCode
import sj.messenger.global.extractBearerToken


class JwtAuthenticationFilter(
    private val authenticationManager: AuthenticationManager,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        when (existsAuthorizationHeader(request)) {
            true -> {
                val bearerToken =
                    extractBearerToken(request) ?: throw IllegalArgumentException("Bearer Token을 가져오는데 실패하였습니다.")
                println("JwtAuthenticationFilter.doFilterInternal - bearer token = ${bearerToken}")
                val authentication = authenticationManager.authenticate(JwtPreAuthenticationToken(bearerToken))

                SecurityContextHolder.getContext().authentication = authentication
            }

            false -> SecurityContextHolder.getContext().authentication = AuthenticatedToken(GuestUserDetails())
        }
        filterChain.doFilter(request, response)
    }

    private fun existsAuthorizationHeader(request: HttpServletRequest): Boolean {
        return request.getHeader(HttpHeaders.AUTHORIZATION) != null
    }
}