package sj.messenger.domain.chat.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import sj.messenger.domain.chat.dto.ChatRoomCreate
import sj.messenger.domain.user.dto.SignUpDto
import sj.messenger.domain.user.service.UserService

@SpringBootTest
@Transactional
class ChatServiceTest(
    @Autowired val chatService: ChatService,
    @Autowired val userService: UserService,
){

    @Test
    fun joinUserTest(){
        // given
        val userId = userService.signUpUser(SignUpDto("alpha@beta.com", "abcd", "1234567890"))
        val chatRoomId = chatService.createChatRoom(ChatRoomCreate(name = "TESTCHATROOM"));

        // when
        chatService.joinChatRoom(chatRoomId,userId)

        // then
        val chatRooms = chatService.findUserChatRooms(userId)
        assertThat(chatRooms.size).isEqualTo(1)
        assertThat(chatRooms[0].participants.size).isEqualTo(1)
        assertThat(chatRooms[0].participants[0].user.id).isEqualTo(userId)
    }
}
