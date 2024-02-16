package sj.messenger.domain.security.filter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpMethod
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.filter.OncePerRequestFilter
import sj.messenger.domain.security.dto.LoginRequest
import sj.messenger.domain.security.dto.LoginResponse
import sj.messenger.domain.security.jwt.JwtProvider
import sj.messenger.domain.security.jwt.UserClaim
import sj.messenger.domain.user.dto.UserDto
import sj.messenger.domain.user.service.UserService

class JwtProvideLoginFilter(
    private val jwtProvider: JwtProvider,
    private val userService: UserService,
) : OncePerRequestFilter() {

    private val objectMapper = ObjectMapper().registerModules(kotlinModule())
    private val matcher = AntPathRequestMatcher("/login",HttpMethod.POST.name())

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val loginRequest = objectMapper.readValue<LoginRequest>(request.inputStream)
        userService.validateLogin(loginRequest)
        val loginUser = userService.findUserByEmail(loginRequest.email)

        val token = jwtProvider.createAccessToken(UserClaim(id = loginUser.id!!, name = loginUser.name))
        val loginResponse = LoginResponse(token = token, user = UserDto(loginUser))
        objectMapper.writeValue(response.writer, loginResponse)

        filterChain.doFilter(request,response)
    }

    // POST /login 요청이 아닐 경우 필터를 무시
    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        return !matcher.matches(request)
    }
}