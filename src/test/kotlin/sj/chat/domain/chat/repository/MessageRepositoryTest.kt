package sj.chat.domain.chat.repository

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.boot.test.context.SpringBootTest
import sj.chat.domain.chat.domain.Message
import java.time.LocalDateTime

@DataMongoTest
class MessageRepositoryTest (
    @Autowired private val messageRepository: MessageRepository
){

    @Test
    fun saveTest(){
        val message = Message(1L, 1L, "123", LocalDateTime.now())
        messageRepository.save(message)

    }
}