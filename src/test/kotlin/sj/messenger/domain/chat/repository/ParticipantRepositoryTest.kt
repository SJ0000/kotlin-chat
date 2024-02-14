package sj.messenger.domain.chat.repository

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import sj.messenger.domain.chat.domain.ChatRoom
import sj.messenger.domain.chat.domain.Participant
import sj.messenger.domain.user.domain.User
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.fixture.generateChatRoom
import sj.messenger.fixture.generateUser

@DataJpaTest
class ParticipantRepositoryTest (
    @Autowired private val participantRepository: ParticipantRepository,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val chatRoomRepository: ChatRoomRepository
){

    @Test
    @DisplayName("getParticipantsByUserId 호출시 chatRoom 또한 같이 조회되어야 한다.")
    fun findByUserIdTest(){
        // given
        val user = generateUser()
        userRepository.save(user)

        for(num in 1..10){
            val chatRoom = generateChatRoom()
            chatRoomRepository.save(chatRoom)
            participantRepository.save(Participant(user,chatRoom))
        }

        // when
        val result = participantRepository.getParticipantsByUserId(user.id!!);

        // then
        Assertions.assertThat(result.size).isEqualTo(10)
    }
}