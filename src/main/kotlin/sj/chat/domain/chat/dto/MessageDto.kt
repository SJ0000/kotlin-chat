package sj.chat.domain.chat.dto

import java.time.LocalDateTime

data class MessageDto(
    val chatRoomId : Long,
    val senderId : Long,
    val content : String,
    val sentAt : LocalDateTime,
)