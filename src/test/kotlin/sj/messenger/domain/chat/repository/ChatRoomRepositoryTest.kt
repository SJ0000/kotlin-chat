package sj.messenger.domain.chat.repository

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import sj.messenger.RepositoryTest
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.util.generateChatRoom
import sj.messenger.util.generateUser

@RepositoryTest
class ChatRoomRepositoryTest (
    @Autowired private val participantRepository: ParticipantRepository,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val chatRoomRepository: ChatRoomRepository,

    @Autowired private val em: TestEntityManager
){

    @Test
    @DisplayName("findWithParticipantsById 호출시 participants 또한 같이 조회되어야 한다.")
    fun findWithParticipantsByIdTest(){
        // given
        val users = (1..3).map { generateUser() }
        userRepository.saveAll(users)
        val chatRoom = generateChatRoom()
        chatRoomRepository.save(chatRoom)
        users.forEach{
         //   chatRoom.join(it)
        }

        em.flush()
        em.clear()

        // when
        val findChatRoom = chatRoomRepository.findWithParticipantsById(chatRoom.id!!)

        // then
        Assertions.assertThat(findChatRoom!!.participants.size).isEqualTo(3)
    }
}