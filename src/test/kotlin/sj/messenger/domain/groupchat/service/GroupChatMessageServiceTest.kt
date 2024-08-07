package sj.messenger.domain.groupchat.service


import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.core.BatchingRabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import sj.messenger.domain.groupchat.repository.GroupMessageRepository
import sj.messenger.util.annotation.ServiceTest
import sj.messenger.util.generateGroupMessage
import sj.messenger.util.truncateMicroSeconds
import java.time.LocalDateTime

@ServiceTest
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
        val message = generateGroupMessage(1L)

        // when
        batchingRabbitTemplate.convertAndSend(groupMessageSaveQueue.name, message)
        Thread.sleep(5000)

        // then
        val messages = groupMessageRepository.findAll()
        val result = messages.find { it.senderId == message.senderId && it.groupChatId == message.groupChatId && it.content == message.content }
        assertThat(result).isNotNull
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