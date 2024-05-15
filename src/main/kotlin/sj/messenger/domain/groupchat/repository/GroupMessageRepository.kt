package sj.messenger.domain.groupchat.repository

import org.bson.types.ObjectId
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import sj.messenger.domain.groupchat.domain.GroupMessage
import java.time.LocalDateTime

@Repository
interface GroupMessageRepository : MongoRepository<GroupMessage, ObjectId?> {
    @Query(value = "{groupChatId: ?0, sentAt : {\$lte: ?1}}", sort = "{sentAt: -1}")
    fun findPreviousMessages(groupChatId: Long, time: LocalDateTime, pageable: Pageable) : List<GroupMessage>
}