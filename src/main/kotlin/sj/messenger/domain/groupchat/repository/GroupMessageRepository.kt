package sj.messenger.domain.groupchat.repository

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import sj.messenger.domain.groupchat.domain.Message

@Repository
interface GroupMessageRepository : MongoRepository<Message, ObjectId?> {

}