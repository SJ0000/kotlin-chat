package sj.messenger.domain.groupchat.dto

import java.time.LocalDateTime

data class InvitationDto(
    val id : String,
    val groupChatId : Long,
    val groupChatName: String,
    val inviterName : String,
    val expiredAt : LocalDateTime
)