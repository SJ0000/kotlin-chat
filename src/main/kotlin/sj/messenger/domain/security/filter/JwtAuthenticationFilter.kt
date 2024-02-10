package sj.messenger.domain.security.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import sj.messenger.domain.security.authentication.AuthenticatedToken
import sj.messenger.domain.security.authentication.JwtPreAuthenticationToken
import sj.messenger.domain.security.authentication.principal.GuestUserDetails


class JwtAuthenticationFilter(
    private val authenticationManager: AuthenticationManager,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try{
            val header = request.getHeader(HttpHeaders.AUTHORIZATION) ?: throw RuntimeException("Authorization header not exists.")
            val bearerToken = parseBearerToken(header)
            val authentication = authenticationManager.authenticate(JwtPreAuthenticationToken(bearerToken))
            SecurityContextHolder.getContext().authentication = authentication
        }catch(e: Exception) {
            SecurityContextHolder.getContext().authentication = AuthenticatedToken(GuestUserDetails())
        }finally{
            filterChain.doFilter(request,response)
        }
    }

    private fun parseBearerToken(header: String): String {
        if (!header.startsWith("Bearer "))
            throw RuntimeException("invalid authorization type.")
        return header.substring("Bearer ".length)
    }
}