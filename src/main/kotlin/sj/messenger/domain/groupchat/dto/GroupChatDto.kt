package sj.messenger.domain.groupchat.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import sj.messenger.domain.user.dto.UserDto

data class GroupChatDto(
    val id: Long,
    val name: String,
    val avatarUrl: String,
    val users: List<UserDto> = listOf()
) {
}

data class GroupChatCreateDto(
    @field:NotEmpty
    @Size(max = 255)
    val name: String
)
