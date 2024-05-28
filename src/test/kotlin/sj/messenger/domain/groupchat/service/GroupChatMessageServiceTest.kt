package sj.messenger.domain.groupchat.service


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
import sj.messenger.domain.groupchat.domain.GroupMessage
import sj.messenger.domain.groupchat.dto.ClientGroupMessageDto
import sj.messenger.domain.groupchat.repository.GroupMessageRepository
import sj.messenger.util.fixture
import sj.messenger.util.generateGroupMessage
import sj.messenger.util.integration.EnableContainers
import sj.messenger.util.truncateMicroSeconds
import java.time.LocalDateTime

@SpringBootTest
@EnableContainers
class GroupChatMessageServiceTest(
    @Autowired val batchingRabbitTemplate: BatchingRabbitTemplate,
    @Autowired val groupMessageSaveQueue: Queue,
    @Autowired val groupChatMessageService: GroupChatMessageService,
    @Autowired val groupMessageRepository: GroupMessageRepository,
){

    @BeforeEach
    fun beforeEach(){
        groupMessageRepository.deleteAll()
    }

    @Test
    @DisplayName("RabbitMQ의 groupMessageSaveQueue로부터 메시지를 읽어서 저장")
    fun saveAllReceivedMessage(){
        // given
        val messageDto : ClientGroupMessageDto = fixture.giveMeOne()

        // when
        batchingRabbitTemplate.convertAndSend(groupMessageSaveQueue.name,messageDto)
        Thread.sleep(5000)

        // then
        val example = Example.of(
            GroupMessage(
                senderId = messageDto.senderId,
                groupChatId = messageDto.groupChatId,
                content = messageDto.content,
                sentAt = messageDto.sentAt
            )
        )
        val findOptional = groupMessageRepository.findOne(example)
        assertThat(findOptional.isPresent).isTrue()
    }

    @Test
    @DisplayName("GroupChat 특정 시점 이전 10개 메시지를 조회")
    fun getPreviousMessages(){
        // given
        val groupChatId = 1L
        val messages = (1..12).map { generateGroupMessage(groupChatId) }
        groupMessageRepository.saveAll(messages)

        // when
        val actual = groupChatMessageService.getPreviousMessages(groupChatId, LocalDateTime.now())

        // then
        val expected = messages.sortedBy { it.sentAt }.subList(2,12)

        actual.zip(expected)
            .forEach{ (a, e) ->
                assertThat(a.groupChatId).isEqualTo(e.groupChatId)
                assertThat(a.content).isEqualTo(e.content)
                assertThat(a.senderId).isEqualTo(e.senderId)
                assertThat(a.receivedAt).isEqualTo(truncateMicroSeconds(e.sentAt))
            }
    }
}