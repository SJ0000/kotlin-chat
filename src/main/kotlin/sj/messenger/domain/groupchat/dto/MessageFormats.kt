package sj.messenger.domain.groupchat.dto

import java.time.LocalDateTime

data class ClientGroupMessageDto(
    val groupChatId : Long,
    val senderId : Long,
    val content : String,
)

data class ServerGroupMessageDto(
    val groupChatId : Long,
    val senderId : Long,
    val content : String,
    val receivedAt : LocalDateTime,
)