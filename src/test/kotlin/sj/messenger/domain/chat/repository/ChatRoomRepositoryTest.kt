package sj.messenger.domain.chat.repository

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.transaction.annotation.Transactional
import sj.messenger.domain.chat.domain.ChatRoom
import sj.messenger.domain.user.domain.User
import sj.messenger.domain.user.repository.UserRepository

@DataJpaTest
class ChatRoomRepositoryTest (
    @Autowired private val participantRepository: ParticipantRepository,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val chatRoomRepository: ChatRoomRepository,

    @Autowired private val em: TestEntityManager
){

    @Test
    @Transactional
    fun findWithParticipantsByIdTest(){
        // given
        val chatRoom = ChatRoom(name = "Test Chat Room")
        chatRoomRepository.save(chatRoom)

        for(num in 1..3){
            val user = User("user${num}", "alpha${num}@beta.com", "1234")
            userRepository.save(user)
            chatRoom.join(user)
        }

        // when
        val findChatRoom = chatRoomRepository.findWithParticipantsById(chatRoom.id!!)

        // then
        Assertions.assertThat(findChatRoom!!.participants.size).isEqualTo(3)
    }
}