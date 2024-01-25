package sj.messenger.domain.chat.domain

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceCreator
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
class Message @PersistenceCreator constructor(
    @Id val id: ObjectId? = null,
    val senderId: Long,
    val chatRoomId: Long,
    val content: String,
    val sentAt: LocalDateTime,
) {

}