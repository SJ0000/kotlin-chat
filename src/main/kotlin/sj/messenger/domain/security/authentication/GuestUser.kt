package sj.messenger.domain.security.authentication

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

class GuestUser : Authentication {

    override fun getName(): String {
        return "AnonymousUser"
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority("ROLE_GUEST"))
    }

    // 일반적으로 비밀번호
    override fun getCredentials(): Any {
        throw UnsupportedOperationException("Guest has no credential.")
    }

    // 인증 요청에 대한 추가 정보 ex) ip주소, 인증 시리얼 넘버 등
    override fun getDetails(): Any {
        throw UnsupportedOperationException("Authentication detail is not supported.")
    }

    // user 정보
    override fun getPrincipal(): Any {
        throw UnsupportedOperationException("Guest has no principal.")
    }

    override fun isAuthenticated(): Boolean {
        return false
    }

    override fun setAuthenticated(isAuthenticated: Boolean) {
        throw UnsupportedOperationException("authentication cannot change")
    }
}