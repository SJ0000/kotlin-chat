package sj.messenger.domain.security.authentication

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails

class AuthenticationToken(
    private val userDetails: UserDetails
) : AbstractAuthenticationToken(userDetails.authorities) {

    override fun getCredentials(): Any {
        return ""
    }

    override fun getPrincipal(): Any {
        return userDetails
    }
}