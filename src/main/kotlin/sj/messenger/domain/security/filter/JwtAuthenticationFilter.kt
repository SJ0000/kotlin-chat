package sj.messenger.domain.security.filter

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import sj.messenger.domain.security.authentication.AuthenticatedToken
import sj.messenger.domain.security.authentication.JwtAuthenticationToken
import sj.messenger.domain.security.authentication.principal.GuestUserDetails


class JwtAuthenticationFilter(
    private val authenticationManager: AuthenticationManager,
) : AbstractAuthenticationProcessingFilter(AntPathRequestMatcher("/**"), authenticationManager) {

    override fun attemptAuthentication(request: HttpServletRequest?, response: HttpServletResponse?): Authentication {
        // Edge case: request null, Authorization header null, invalid token
        if(request == null){
            return AuthenticatedToken(GuestUserDetails())
        }

        val header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if(header.isNullOrBlank()){
            return AuthenticatedToken(GuestUserDetails())
        }

        val jwtToken = parseBearerToken(header)
        return authenticationManager.authenticate(JwtAuthenticationToken(jwtToken))
    }

    private fun parseBearerToken(header: String): String {
        if (!header.startsWith("Bearer "))
            throw RuntimeException("invalid authorization type.")
        return header.substring("Bearer ".length)
    }
}