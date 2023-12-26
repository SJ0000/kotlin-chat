package sj.chat.domain.security.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class SignUp(
    @Email
    val email : String,
    @Size(min = 1, max = 20)
    val name : String,
    @Size(min = 10, max = 20)
    val password : String,
)