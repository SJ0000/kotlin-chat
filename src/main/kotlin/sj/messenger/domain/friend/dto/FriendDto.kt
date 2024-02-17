package sj.messenger.domain.friend.dto

import java.time.LocalDateTime

data class FriendDto (
    val from: Long,
    val to: Long,
    val receivedAt: LocalDateTime
)