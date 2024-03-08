package sj.messenger.domain.groupchat.controller

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import sj.messenger.domain.directchat.dto.DirectMessageType
import sj.messenger.domain.directchat.dto.ReceivedDirectMessageDto
import sj.messenger.domain.directchat.dto.SentDirectMessageDto
import sj.messenger.domain.groupchat.dto.ReceivedGroupMessageDto
import sj.messenger.domain.groupchat.dto.SentGroupMessageDto
import sj.messenger.util.config.TestStompClient
import sj.messenger.util.config.TestStompClientConfig
import sj.messenger.util.integration.EnableContainers
import java.time.LocalDateTime

@Import(TestStompClientConfig::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@EnableContainers
class GroupChatStompControllerTest(
    @Autowired val client : TestStompClient
){

    @Test
    fun sendMessage(){
        val source = "/app/group-message"
        val destination = "/topic/group-chat/1"
        val message = SentGroupMessageDto(
            groupChatId = 1L,
            senderId = 1L,
            content = "1234",
            sentAt = LocalDateTime.now()
        )

        val received = client.sendAndReceive<ReceivedGroupMessageDto>(source, destination, message)
        with(received){
            Assertions.assertThat(groupChatId).isEqualTo(message.groupChatId)
            Assertions.assertThat(senderId).isEqualTo(message.senderId)
            Assertions.assertThat(content).isEqualTo(message.content)
        }
    }
}