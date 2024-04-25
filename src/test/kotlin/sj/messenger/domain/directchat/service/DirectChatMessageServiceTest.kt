package sj.messenger.domain.directchat.service

import com.navercorp.fixturemonkey.kotlin.giveMeOne
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.core.BatchingRabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
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
    @DisplayName("RabbitMQ의 directMessageSaveQueue에 있는 메시지를 batch로 읽어와 저장한다")
    fun saveAllReceivedMessage(){
        // given
        val countBeforeSave =  directMessageRepository.count()
        val messageDto : SentDirectMessageDto = fixture.giveMeOne()

        // when
        batchingRabbitTemplate.convertAndSend(directMessageSaveQueue.name,messageDto)
        Thread.sleep(5000)

        // then
        val countAfterSave = directMessageRepository.count()
        assertThat(countAfterSave).isEqualTo(countBeforeSave+1)
    }

}