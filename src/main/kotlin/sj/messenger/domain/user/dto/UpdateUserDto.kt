package sj.messenger.domain.user.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class UpdateUserDto (
    @field:NotBlank
    @field:Size(max = 20)
    val name: String,

    @field:NotNull
    val avatarUrl: String,

    @field:Size(max = 255)
    val statusMessage: String,

    @field:NotBlank
    @field:Size(max = 255)
    val publicIdentifier: String,
)