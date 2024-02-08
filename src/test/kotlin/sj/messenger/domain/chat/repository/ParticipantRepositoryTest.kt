package sj.messenger.domain.chat.repository

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import sj.messenger.domain.chat.domain.ChatRoom
import sj.messenger.domain.chat.domain.Participant
import sj.messenger.domain.user.domain.User
import sj.messenger.domain.user.repository.UserRepository

@DataJpaTest
class ParticipantRepositoryTest (
    @Autowired private val participantRepository: ParticipantRepository,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val chatRoomRepository: ChatRoomRepository
){

    @Test
    fun findByUserIdTest(){
        // given
        val user = User("user", "alpha@beta.com", "1234")
        userRepository.save(user)

        for(num in 1..10){
            val chatRoom = ChatRoom(name = "ChatRoom ${num}")
            chatRoomRepository.save(chatRoom)
            participantRepository.save(Participant(user,chatRoom))
        }

        // when
        val result = participantRepository.getParticipantsByUserId(user.id!!);

        // then
        Assertions.assertThat(result.size).isEqualTo(10)
    }
}