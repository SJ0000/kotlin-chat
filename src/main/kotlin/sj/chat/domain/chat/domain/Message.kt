package sj.chat.domain.chat.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
class Message (
    val senderId : Long,
    val chatRoomId : Long,
    val content : String,
    val sentAt : LocalDateTime,
){
    @Id
    val id : String? = null
}