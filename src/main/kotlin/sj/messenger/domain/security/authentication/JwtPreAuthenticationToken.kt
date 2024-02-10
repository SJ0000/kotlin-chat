package sj.messenger.domain.security.authentication

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils

// AuthenticationProvider 에 의해 인증되기 전의 인증 토큰
class JwtPreAuthenticationToken(
    private val jwtToken: String,
) : AbstractAuthenticationToken(AuthorityUtils.NO_AUTHORITIES) {

    override fun getCredentials(): Any {
        return jwtToken
    }

    override fun getPrincipal(): Any {
        return "Not identified"
    }
}