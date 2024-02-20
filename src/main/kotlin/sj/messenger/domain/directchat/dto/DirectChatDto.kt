package sj.messenger.domain.directchat.dto

import sj.messenger.domain.user.dto.UserDto

data class DirectChatDto(
    val id: Long,
    val otherUser: UserDto
)