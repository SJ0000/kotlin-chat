package sj.messenger.domain.chat.dto

import sj.messenger.domain.user.dto.UserDto

data class ChatRoomDto (
    val id : Long,
    val users : List<UserDto> = listOf()
)
