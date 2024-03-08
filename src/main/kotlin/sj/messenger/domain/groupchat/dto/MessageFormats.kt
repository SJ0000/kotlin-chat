package sj.messenger.domain.groupchat.dto

import java.time.LocalDateTime

data class SentGroupMessageDto(
    val groupChatId : Long,
    val senderId : Long,
    val content : String,
    val sentAt : LocalDateTime,
)

data class ReceivedGroupMessageDto(
    val id: String,
    val groupChatId : Long,
    val senderId : Long,
    val content : String,
    val receivedAt : LocalDateTime,
)