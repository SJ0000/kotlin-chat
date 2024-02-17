package sj.messenger.domain.user.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UpdateUserDto (
    @Size(min = 1, max = 20)
    val name: String,
    @NotBlank
    val avatarUrl: String,
    val statusMessage: String,
    val publicIdentifier: String,
)