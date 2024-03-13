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
class GroupMessageRepositoryTest (
    @Autowired private val groupMessageRepository: GroupMessageRepository
){

    @Test
    @Rollback
    fun saveTest(){
        val message = Message(senderId = 1L, chatRoomId = 1L, content =  "123", sentAt =  LocalDateTime.now())
        val savedMessage = groupMessageRepository.save(message)
        println(savedMessage.id?.toString())
    }
}