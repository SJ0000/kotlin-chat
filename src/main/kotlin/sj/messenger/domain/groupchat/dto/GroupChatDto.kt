package sj.messenger.domain.groupchat.dto

import jakarta.validation.constraints.NotEmpty
import sj.messenger.domain.user.dto.UserDto

data class GroupChatDto(
    val id: Long,
    val name: String,
    val avatarUrl: String,
    val users: List<UserDto>
) {
}

data class GroupChatCreate(
    @field:NotEmpty
    val name: String
)
