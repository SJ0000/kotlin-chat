package sj.messenger.domain.groupchat.service


import com.navercorp.fixturemonkey.kotlin.giveMeOne
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.core.BatchingRabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Example
import sj.messenger.domain.groupchat.domain.GroupMessage
import sj.messenger.domain.groupchat.dto.SentGroupMessageDto
import sj.messenger.domain.groupchat.repository.GroupMessageRepository
import sj.messenger.util.fixture
import sj.messenger.util.integration.EnableContainers

@SpringBootTest
@EnableContainers
class GroupChatMessageServiceTest(
    @Autowired val batchingRabbitTemplate: BatchingRabbitTemplate,
    @Autowired val groupMessageSaveQueue: Queue,
    @Autowired val groupMessageRepository: GroupMessageRepository,
){

    @Test
    @DisplayName("RabbitMQ의 groupMessageSaveQueue로부터 메시지를 읽어서 저장")
    fun saveAllReceivedMessage(){
        // given
        val messageDto : SentGroupMessageDto = fixture.giveMeOne()

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
}