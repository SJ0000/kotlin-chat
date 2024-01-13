package sj.messenger.domain.security.filter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.GenericFilterBean
import sj.messenger.domain.security.dto.LoginRequest
import sj.messenger.domain.security.dto.LoginResponse
import sj.messenger.domain.security.jwt.JwtProvider
import sj.messenger.domain.user.dto.UserDto
import sj.messenger.domain.user.service.UserService

class JwtProvideLoginFilter(
    private val jwtProvider: JwtProvider,
    private val userService: UserService,
) : GenericFilterBean() {

    private val objectMapper = ObjectMapper()

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        this.doFilter(request as HttpServletRequest, response as HttpServletResponse, chain!!)
    }

    private fun doFilter(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain){
        // post, login인지 check?  filter chain에 url 등록 기능 없다면
        val loginRequest = objectMapper.readValue<LoginRequest>(request.inputStream)
        userService.validateLogin(loginRequest)
        val loginUser = userService.findUser(loginRequest.email)

        val token = jwtProvider.createAccessToken(loginUser.id!!)
        val loginResponse = LoginResponse(token = token, user = UserDto(loginUser))
        objectMapper.writeValue(response.writer,loginResponse)
    }
}