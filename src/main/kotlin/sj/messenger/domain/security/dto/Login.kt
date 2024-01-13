package sj.messenger.domain.security.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size
import sj.messenger.domain.user.dto.UserDto

data class LoginRequest(
    @Email
    val email : String,
    @Size(min = 10, max = 20)
    val password : String,
)

data class LoginResponse(
    val token : String,
    val user : UserDto
)