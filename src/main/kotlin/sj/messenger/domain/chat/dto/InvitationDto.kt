package sj.messenger.domain.chat.dto

import java.time.LocalDateTime

data class InvitationDto(
    val id : String,
    val chatRoomId : Long,
    val chatRoomName: String,
    val inviterName : String,
    val expiredAt : LocalDateTime
)