package sj.messenger.domain.groupchat.service


import org.assertj.core.api.Assertions.assertThatNoException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import sj.messenger.domain.groupchat.dto.SentGroupMessageDto
import sj.messenger.util.integration.EnableContainers
import sj.messenger.util.randomString
import java.time.LocalDateTime

@SpringBootTest
@EnableContainers
class GroupChatMessageServiceTest(
    @Autowired val groupChatMessageService: GroupChatMessageService
){

    @Test
    @DisplayName("그룹 채팅 메시지 저장")
    fun saveMessage(){
        val message = SentGroupMessageDto(
            groupChatId = 1L,
            senderId = 2L,
            content = randomString(1,100),
            sentAt = LocalDateTime.now()
        )
        assertThatNoException().isThrownBy {
            groupChatMessageService.saveMessage(message)
        }
    }
}