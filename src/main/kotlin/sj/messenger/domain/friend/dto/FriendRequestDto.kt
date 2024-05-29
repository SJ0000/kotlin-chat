package sj.messenger.domain.friend.dto

import jakarta.validation.constraints.NotBlank

data class FriendRequestDto(
    @field:NotBlank
    val publicIdentifier: String
)
