package sj.messenger.domain.chat.repository

import org.bson.types.ObjectId
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import sj.messenger.domain.chat.domain.Message

@Repository
interface MessageRepository : CrudRepository<Message, ObjectId?> {

}