package sj.messenger.domain.directchat.service

import com.navercorp.fixturemonkey.kotlin.giveMeOne
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.core.BatchingRabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Example
import sj.messenger.domain.directchat.domain.DirectMessage
import sj.messenger.domain.directchat.dto.SentDirectMessageDto
import sj.messenger.domain.directchat.repository.DirectMessageRepository
import sj.messenger.util.fixture
import sj.messenger.util.integration.EnableContainers

@SpringBootTest
@EnableContainers
class DirectChatMessageServiceTest(
    @Autowired val batchingRabbitTemplate: BatchingRabbitTemplate,
    @Autowired val directMessageSaveQueue: Queue,
    @Autowired val directMessageRepository: DirectMessageRepository,
){

    @Test
    @DisplayName("RabbitMQ의 directMessageSaveQueue로부터 메시지를 읽어와 저장")
    fun saveAllReceivedMessage(){
        // given
        val messageDto : SentDirectMessageDto = fixture.giveMeOne()

        // when
        batchingRabbitTemplate.convertAndSend(directMessageSaveQueue.name,messageDto)
        Thread.sleep(5000)

        // then
        val example = Example.of(
            DirectMessage(
                senderId = messageDto.senderId,
                directChatId = messageDto.directChatId,
                content = messageDto.content,
                sentAt = messageDto.sentAt
            )
        )
        val findOptional = directMessageRepository.findOne(example)
        assertThat(findOptional.isPresent).isTrue()
    }
}