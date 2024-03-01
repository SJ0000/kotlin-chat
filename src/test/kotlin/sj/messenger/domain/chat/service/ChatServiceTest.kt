package sj.messenger.domain.chat.service

import com.navercorp.fixturemonkey.kotlin.giveMeOne
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import sj.messenger.domain.chat.domain.ChatRoom
import sj.messenger.domain.chat.dto.ChatRoomCreate
import sj.messenger.domain.chat.repository.ChatRoomRepository
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.util.fixture
import sj.messenger.util.generateChatRoom
import sj.messenger.util.generateUser

@SpringBootTest
@Transactional
class ChatServiceTest(
    @Autowired val chatService: ChatService,
    @Autowired val userRepository: UserRepository,
    @Autowired val chatRoomRepository: ChatRoomRepository,

) {

    @Test
    @DisplayName("존재하지 않는 chatRoom 조회시 예외 발생")
    fun getChatRoomError() {
        // expected
        assertThatThrownBy { chatService.getChatRoom(1L) }
            .isInstanceOf(RuntimeException::class.java)
    }

    @Test
    @DisplayName("사용자가 대화방에 정상적으로 참여")
    fun joinChatRoom(){
        // given
        val user = generateUser()
        userRepository.save(user)
        val chatRoom = chatRoomRepository.save(ChatRoom(name = fixture.giveMeOne()))

        // when
        chatService.joinChatRoom(chatRoom.id!!, user.id!!)

        // then
        val isParticipant = chatRoomRepository.findByIdOrNull(chatRoom.id!!)?.isParticipant(user.id!!)
        assertThat(isParticipant).isTrue()
    }

    @Test
    @DisplayName("사용자가 참여하고자 하는 대화방에 이미 참여중인 경우 예외 발생")
    fun joinChatRoomError(){
        // given
        val user = generateUser()
        userRepository.save(user)
        val chatRoom = chatRoomRepository.save(ChatRoom(name = fixture.giveMeOne()))
        chatRoom.join(user)

        // expected
        assertThatThrownBy {
            chatService.joinChatRoom(chatRoom.id!!,user.id!!)
        }.isInstanceOf(RuntimeException::class.java)
    }

    @Test
    @DisplayName("대화방 정상 생성")
    fun createChatRoom(){
        // given
        val chatRoomCreate = fixture.giveMeOne<ChatRoomCreate>()

        // when
        val chatRoomId = chatService.createChatRoom(chatRoomCreate)

        // then
        val findChatRoom = chatRoomRepository.findByIdOrNull(chatRoomId)
        assertThat(findChatRoom).isNotNull
        assertThat(findChatRoom?.name).isEqualTo(chatRoomCreate.name)
    }

    @Test
    @DisplayName("특정 사용자의 참가한 대화방 리스트 조회")
    fun findUserChatRooms(){
        // given
        val user = generateUser()
        userRepository.save(user)
        val chatRooms = (1..3).map { generateChatRoom() }
        chatRooms.forEach { it.join(user) }
        chatRoomRepository.saveAll(chatRooms)

        // when
        val userChatRooms = chatService.findUserChatRooms(user.id!!)

        // then
        assertThat(userChatRooms.size).isEqualTo(chatRooms.size)
    }

}
