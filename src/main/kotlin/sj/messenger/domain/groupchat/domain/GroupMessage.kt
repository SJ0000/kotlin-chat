package sj.messenger.domain.groupchat.domain

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceCreator
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
class GroupMessage @PersistenceCreator constructor(
    val senderId: Long,
    val groupChatId: Long,
    val content: String,
    val sentAt: LocalDateTime,
    @Id val id: ObjectId? = null,
) {

}