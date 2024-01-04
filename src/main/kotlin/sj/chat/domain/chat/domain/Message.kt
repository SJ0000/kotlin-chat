package sj.chat.domain.chat.domain

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceCreator
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
class Message @PersistenceCreator constructor(
    @Id val id: String? = null,
    val senderId: Long,
    val chatRoomId: Long,
    val content: String,
    val sentAt: LocalDateTime,
) {

}