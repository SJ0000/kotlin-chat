package sj.messenger.domain.chat.dto

import java.time.LocalDateTime

data class SentDirectMessageDto(
    val directChatId : Long,
    val senderId : Long,
    val content : String,
    val sentAt : LocalDateTime,
)

data class ReceivedDirectMessageDto(
    val id: String,
    val directChatId : Long,
    val senderId : Long,
    val content : String,
    val receivedAt: LocalDateTime
)

data class SentMessageDto(
    val chatRoomId : Long,
    val senderId : Long,
    val content : String,
    val sentAt : LocalDateTime,
)

data class ReceivedMessageDto(
    val id: String,
    val chatRoomId : Long,
    val senderId : Long,
    val content : String,
    val receivedAt : LocalDateTime,
)