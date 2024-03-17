package sj.messenger.domain.groupchat.controller

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import sj.messenger.domain.groupchat.dto.ReceivedGroupMessageDto
import sj.messenger.domain.groupchat.dto.SentGroupMessageDto
import sj.messenger.domain.groupchat.repository.GroupMessageRepository
import sj.messenger.util.config.TestStompClient
import sj.messenger.util.integration.IntegrationTest
import java.time.LocalDateTime

@IntegrationTest
class GroupChatStompControllerTest(
    @Autowired val client : TestStompClient,
    @Autowired val groupMessageRepository: GroupMessageRepository,
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