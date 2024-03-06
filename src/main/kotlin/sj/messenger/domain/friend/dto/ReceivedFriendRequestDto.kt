package sj.messenger.domain.friend.dto

import sj.messenger.domain.user.dto.UserDto
import java.time.LocalDateTime

data class ReceivedFriendRequestDto (
    val id: Long,
    val fromUser: UserDto,
    val receivedAt: LocalDateTime
)