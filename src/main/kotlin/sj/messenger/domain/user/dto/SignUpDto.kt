package sj.messenger.domain.user.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size

data class SignUpDto(
    @field:Email
    @field:Size(max = 255)
    val email: String,
    @field:Size(min = 1, max = 20)
    val name: String,
    @field:Size(min = 10, max = 20)
    val password: String,
)