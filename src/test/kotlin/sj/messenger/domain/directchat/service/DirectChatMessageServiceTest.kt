package sj.messenger.domain.directchat.service

import com.navercorp.fixturemonkey.kotlin.giveMeOne
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.core.BatchingRabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Example
import sj.messenger.domain.directchat.domain.DirectMessage
import sj.messenger.domain.directchat.dto.ClientDirectMessageDto
import sj.messenger.domain.directchat.repository.DirectMessageRepository
import sj.messenger.util.fixture
import sj.messenger.util.generateDirectMessage
import sj.messenger.util.integration.EnableContainers
import sj.messenger.util.truncateMicroSeconds
import java.time.LocalDateTime

@SpringBootTest
@EnableContainers
class DirectChatMessageServiceTest(
    @Autowired val batchingRabbitTemplate: BatchingRabbitTemplate,
    @Autowired val directMessageSaveQueue: Queue,
    @Autowired val directMessageRepository: DirectMessageRepository,
    @Autowired val directChatMessageService: DirectChatMessageService,
) {

    @BeforeEach
    fun beforeEach(){
        directMessageRepository.deleteAll()
    }

    @Test
    @DisplayName("RabbitMQ의 directMessageSaveQueue로부터 메시지를 읽어와 저장")
    fun saveAllReceivedMessage() {
        // given
        val message: DirectMessage = generateDirectMessage(1L)

        // when
        batchingRabbitTemplate.convertAndSend(directMessageSaveQueue.name, message)
        Thread.sleep(5000)

        // then
        val messages = directMessageRepository.findAll()
        val result =
            messages.find { it.senderId == message.senderId && it.directChatId == message.directChatId && it.content == message.content }
        assertThat(result).isNotNull
    }

    @Test
    @DisplayName("DirectChat의 특정 시간 이전 메시지를 10개 조회")
    fun getPreviousMessages() {
        // given
        val directChatId = 1L
        val directMessages = (1..12).map {
            generateDirectMessage(directChatId)
        }.sortedBy { it.sentAt }

        directMessageRepository.saveAll(directMessages)

        // when
        val previousMessages = directChatMessageService.getPreviousMessages(directChatId, LocalDateTime.now())

        // then
        assertThat(previousMessages.size).isEqualTo(10)

        previousMessages.forEach{
            println("previous Message = ${it.senderId}, ${it.receivedAt}")
        }

        directMessages.forEach{
            println("directMessages = ${it.senderId}, ${it.sentAt}")
        }

        directMessages
            .subList(2, 11)
            .zip(previousMessages)
            .forEach {
                val message = it.first
                val dto = it.second
                assertThat(message.directChatId).isEqualTo(dto.directChatId)
                assertThat(message.senderId).isEqualTo(dto.senderId)
                assertThat(message.content).isEqualTo(dto.content)
                assertThat(truncateMicroSeconds(message.sentAt)).isEqualTo(dto.receivedAt)
            }
    }
}