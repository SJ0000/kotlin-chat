package sj.messenger.domain.directchat.controller

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import sj.messenger.domain.directchat.dto.DirectMessageType
import sj.messenger.domain.directchat.dto.ReceivedDirectMessageDto
import sj.messenger.domain.directchat.dto.SentDirectMessageDto
import sj.messenger.util.config.TestStompClient
import sj.messenger.util.config.TestStompClientConfig
import sj.messenger.util.integration.EnableContainers
import java.time.LocalDateTime

@Import(TestStompClientConfig::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@EnableContainers
class DirectChatStompControllerTest(
    @Autowired val client: TestStompClient
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
    }
}