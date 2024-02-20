package sj.messenger.domain.directchat.repository

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import sj.messenger.domain.directchat.domain.DirectMessage

@Repository
interface DirectMessageRepository : MongoRepository<DirectMessage, ObjectId?> {

}