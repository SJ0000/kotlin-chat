package sj.messenger.domain.directchat.controller

import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.repository.findByIdOrNull
import sj.messenger.domain.directchat.dto.DirectMessageType
import sj.messenger.domain.directchat.dto.ReceivedDirectMessageDto
import sj.messenger.domain.directchat.dto.SentDirectMessageDto
import sj.messenger.domain.directchat.repository.DirectMessageRepository
import sj.messenger.util.config.TestStompClient
import sj.messenger.util.config.TestStompClientConfig
import sj.messenger.util.integration.EnableContainers
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Import(TestStompClientConfig::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@EnableContainers
class DirectChatStompControllerTest(
    @Autowired val client: TestStompClient,
    @Autowired val directMessageRepository: DirectMessageRepository,
) {
    @Test
    fun directMessage(){
        val source = "/app/direct-message"
        val destination = "/topic/direct-chat/1"
        val message = SentDirectMessageDto(
            directChatId = 1L,
            messageType = DirectMessageType.MESSAGE,
            senderId = 1L,
            receiverId = 1L,
            content = "1234",
            sentAt = LocalDateTime.now()
        )
        val received = client.sendAndReceive<ReceivedDirectMessageDto>(source, destination, message)

        with(received){
            assertThat(directChatId).isEqualTo(message.directChatId)
            assertThat(messageType).isEqualTo(message.messageType)
            assertThat(senderId).isEqualTo(message.senderId)
            assertThat(content).isEqualTo(message.content)
        }

        val savedMessage = directMessageRepository.findByIdOrNull(ObjectId(received.id))!!
        assertThat(savedMessage.directChatId).isEqualTo(message.directChatId)
        assertThat(savedMessage.senderId).isEqualTo(message.senderId)
        assertThat(savedMessage.content).isEqualTo(message.content)
        assertThat(savedMessage.sentAt).isEqualTo(message.sentAt.truncatedTo(ChronoUnit.MILLIS))
    }
}