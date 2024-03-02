package sj.messenger.domain.chat.repository

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import sj.messenger.domain.chat.domain.Message
import java.time.LocalDateTime

@DataMongoTest
@Testcontainers
class MessageRepositoryTest (
    @Autowired private val messageRepository: MessageRepository
){
    /*
        내용 정리
         - 공식 문서에서 rdb에서는 왜 됐는지, (jdbc support warning)
         - mongodb는 왜 안됐는지, (testcontainer network 왜 포트 지정을 할 수 없는지)
        TODO : Redis도 이번에 한것처럼 적용하고, 시행착오 정리하기
     */

    companion object{
        @Container
        val mongoDBContainer: MongoDBContainer = MongoDBContainer("mongo:latest")
            .withExposedPorts(27017)
            .withReuse(true)

        @JvmStatic
        @DynamicPropertySource
        fun applyContainerConnection(registry: DynamicPropertyRegistry){
            registry.add("spring.data.mongodb.host"){
                mongoDBContainer.host
            }
            registry.add("spring.data.mongodb.port"){
                mongoDBContainer.getMappedPort(27017)
            }
        }

        init{
            mongoDBContainer.start()
        }
    }

    @Test
    @Rollback
    fun saveTest(){
        val message = Message(senderId = 1L, chatRoomId = 1L, content =  "123", sentAt =  LocalDateTime.now())
        val savedMessage = messageRepository.save(message)
        println(savedMessage.id?.toString())
    }
}