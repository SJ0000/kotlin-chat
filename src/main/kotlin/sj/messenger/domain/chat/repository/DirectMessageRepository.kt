package sj.messenger.domain.chat.repository

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import sj.messenger.domain.chat.domain.Message

@Repository
interface MessageRepository : MongoRepository<Message, ObjectId?> {

}