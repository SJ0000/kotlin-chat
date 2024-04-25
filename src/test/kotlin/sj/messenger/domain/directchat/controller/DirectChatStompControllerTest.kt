package sj.messenger.domain.directchat.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import sj.messenger.domain.directchat.dto.DirectMessageType
import sj.messenger.domain.directchat.dto.ReceivedDirectMessageDto
import sj.messenger.domain.directchat.dto.SentDirectMessageDto
import sj.messenger.domain.directchat.repository.DirectMessageRepository
import sj.messenger.util.config.TestStompClient
import sj.messenger.util.integration.IntegrationTest
import java.time.LocalDateTime

@IntegrationTest
class DirectChatStompControllerTest(
    @Autowired val client: TestStompClient,
    @Autowired val directMessageRepository: DirectMessageRepository,
) {
    @Test
    fun directMessage(){
        // given
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

        // when
        val received = client.sendAndReceive<ReceivedDirectMessageDto>(source, destination, message)

        // then
        with(received){
            assertThat(directChatId).isEqualTo(message.directChatId)
            assertThat(messageType).isEqualTo(message.messageType)
            assertThat(senderId).isEqualTo(message.senderId)
            assertThat(content).isEqualTo(message.content)
        }
    }
}