package sj.messenger.domain.directchat.repository

import org.bson.types.ObjectId
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import sj.messenger.domain.directchat.domain.DirectMessage
import java.time.LocalDateTime

@Repository
interface DirectMessageRepository : MongoRepository<DirectMessage, ObjectId?> {
    @Query(value= "{directChatId : ?0, sentAt: {\$lte: ?1}}", sort = "{sentAt: -1}")
    fun findPreviousMessages(directChatId: Long, time: LocalDateTime, pageable: Pageable) :List<DirectMessage>
}