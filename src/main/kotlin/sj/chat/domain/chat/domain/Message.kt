package sj.chat.domain.chat.domain

import java.time.LocalDateTime

class Message (
    val id : Long,
    val senderId : Long,
    val chatRoomId : Long,
    val content : String,
    val createdAt : LocalDateTime,
){


}