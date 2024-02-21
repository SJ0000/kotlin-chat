package sj.messenger.domain.directchat.dto

import java.time.LocalDateTime

data class SentDirectMessageDto(
    val directChatId : Long,
    val messageType: DirectMessageType,
    val senderId : Long,
    val receiverId: Long,
    val content : String,
    val sentAt : LocalDateTime,
)

data class ReceivedDirectMessageDto(
    val id: String,
    val directChatId : Long,
    val messageType: DirectMessageType,
    val senderId : Long,
    val content : String,
    val receivedAt: LocalDateTime
)

enum class DirectMessageType{
    MESSAGE, // 단순 메시지
    NOTIFICATION // 알림
}