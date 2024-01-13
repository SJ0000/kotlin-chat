package sj.messenger.domain.user.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size

data class SignUpDto(
    @Email
    val email : String,
    @Size(min = 1, max = 20)
    val name : String,
    @Size(min = 10, max = 20)
    val password : String,
)