package sj.messenger.domain.user.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UpdateUserDto (
    @field:Size(min = 1, max = 20)
    val name: String,
    @field:NotBlank
    val avatarUrl: String,
    @field:Size(min = 1, max = 255)
    val statusMessage: String,
    @field:Size(min = 1, max = 255)
    val publicIdentifier: String,
)