package sj.messenger.domain.security.authentication

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

class AuthenticatedUser(
    private val userToken : UserToken
) : Authentication {

    override fun getName(): String {
        return userToken.name
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority("ROLE_USER"))
    }

    // 일반적으로 비밀번호
    override fun getCredentials(): Any {
        throw UnsupportedOperationException("credentials not supported.")
    }

    // 인증 요청에 대한 추가 정보 ex) ip주소, 인증 시리얼 넘버 등
    override fun getDetails(): Any {
        throw UnsupportedOperationException("Authentication detail is not supported.")
    }

    // user 정보
    override fun getPrincipal(): Any {
        return userToken
    }

    override fun isAuthenticated(): Boolean {
        return true
    }

    override fun setAuthenticated(isAuthenticated: Boolean) {
        throw UnsupportedOperationException("authentication cannot change")
    }
}