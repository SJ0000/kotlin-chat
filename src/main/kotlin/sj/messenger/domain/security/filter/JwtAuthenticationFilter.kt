package sj.messenger.domain.security.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import sj.messenger.domain.security.authentication.AuthenticatedToken
import sj.messenger.domain.security.authentication.JwtPreAuthenticationToken
import sj.messenger.domain.security.authentication.principal.GuestUserDetails
import sj.messenger.global.extractBearerToken


class JwtAuthenticationFilter(
    private val authenticationManager: AuthenticationManager,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try{
            val bearerToken = extractBearerToken(request) ?: throw IllegalArgumentException("Bearer Token을 가져오는데 실패하였습니다.")
            val authentication = authenticationManager.authenticate(JwtPreAuthenticationToken(bearerToken))
            SecurityContextHolder.getContext().authentication = authentication
        }catch(e: Exception) {
            SecurityContextHolder.getContext().authentication = AuthenticatedToken(GuestUserDetails())
        }finally{
            filterChain.doFilter(request,response)
        }
    }
}