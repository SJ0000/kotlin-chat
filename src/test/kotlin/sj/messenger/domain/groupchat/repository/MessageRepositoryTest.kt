package sj.messenger.domain.groupchat.repository

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.annotation.Rollback
import sj.messenger.domain.groupchat.domain.Message
import sj.messenger.util.testcontainer.annotation.EnableMongoContainer
import java.time.LocalDateTime

@DataMongoTest
@EnableMongoContainer
class MessageRepositoryTest (
    @Autowired private val messageRepository: MessageRepository
){

    @Test
    @Rollback
    fun saveTest(){
        val message = Message(senderId = 1L, chatRoomId = 1L, content =  "123", sentAt =  LocalDateTime.now())
        val savedMessage = messageRepository.save(message)
        println(savedMessage.id?.toString())
    }
}