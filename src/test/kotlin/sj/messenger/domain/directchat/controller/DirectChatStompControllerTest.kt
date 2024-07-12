package sj.messenger.domain.directchat.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import sj.messenger.domain.directchat.dto.DirectMessageType
import sj.messenger.domain.directchat.dto.ServerDirectMessageDto
import sj.messenger.domain.directchat.dto.ClientDirectMessageDto
import sj.messenger.domain.directchat.repository.DirectMessageRepository
import sj.messenger.util.websocket.TestStompClient
import sj.messenger.util.integration.IntegrationTest

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
        val message = ClientDirectMessageDto(
            directChatId = 1L,
            messageType = DirectMessageType.MESSAGE,
            senderId = 1L,
            receiverId = 1L,
            content = "1234",
        )

        // when
        val received = client.sendAndReceive<ServerDirectMessageDto>(source, destination, message)

        // then
        with(received){
            assertThat(directChatId).isEqualTo(message.directChatId)
            assertThat(messageType).isEqualTo(message.messageType)
            assertThat(senderId).isEqualTo(message.senderId)
            assertThat(content).isEqualTo(message.content)
        }
    }
}