package sj.messenger.domain.chat.dto

import jakarta.validation.constraints.NotEmpty
import sj.messenger.domain.chat.domain.ChatRoom
import sj.messenger.domain.user.dto.UserDto

data class ChatRoomDto(
    val id: Long,
    val name: String,
    val avatarUrl: String,
    val users: List<UserDto>
) {
}

data class ChatRoomCreate(
    @NotEmpty
    val name: String
)
