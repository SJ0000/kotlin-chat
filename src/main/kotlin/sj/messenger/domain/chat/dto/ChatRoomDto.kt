package sj.messenger.domain.chat.dto

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
    val name: String
)
