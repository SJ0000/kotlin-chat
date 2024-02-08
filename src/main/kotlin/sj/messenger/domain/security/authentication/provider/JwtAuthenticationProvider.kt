package sj.messenger.domain.security.authentication.provider

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import sj.messenger.domain.security.authentication.AuthenticatedToken
import sj.messenger.domain.security.authentication.JwtAuthenticationToken
import sj.messenger.domain.security.authentication.principal.LoginUserDetails
import sj.messenger.domain.security.jwt.JwtParser

class JwtAuthenticationProvider(
    private val jwtParser: JwtParser
): AuthenticationProvider {

    override fun authenticate(authentication: Authentication?): Authentication {
        val jwtAuthenticationToken = authentication as JwtAuthenticationToken
        val jwtToken = jwtAuthenticationToken.credentials as String
        val userClaim = jwtParser.validateAndGetUserClaim(jwtToken)
        return AuthenticatedToken(LoginUserDetails(userClaim))
    }

    override fun supports(authentication: Class<*>?): Boolean {
        if(authentication == null)
            return false
        return (JwtAuthenticationToken::class.java.isAssignableFrom(authentication))
    }
}