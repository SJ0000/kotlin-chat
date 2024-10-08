package sj.messenger.domain.security.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import sj.messenger.domain.security.dto.LoginRequest
import sj.messenger.domain.security.dto.LoginResponse
import sj.messenger.domain.security.jwt.JwtProvider
import sj.messenger.domain.security.jwt.UserClaim
import sj.messenger.domain.user.dto.UserDto
import sj.messenger.domain.user.service.UserService

private val logger = KotlinLogging.logger {  }

@RestController
class AuthenticationController (
    private val userService: UserService,
    private val jwtProvider: JwtProvider,
){

    @PostMapping("/login")
    fun login(@Valid @RequestBody loginRequest: LoginRequest) : ResponseEntity<LoginResponse>{
        userService.validateLogin(loginRequest)
        val loginUser = userService.findUserByEmail(loginRequest.email)
        val token = jwtProvider.createAccessToken(UserClaim(id = loginUser.id!!, name = loginUser.name))
        val loginResponse = LoginResponse(token = token, user = UserDto(loginUser))

        logger.info { "사용자(id = ${loginUser.id}) 가 로그인하였습니다." }
        return ResponseEntity.ok(loginResponse)
    }
}