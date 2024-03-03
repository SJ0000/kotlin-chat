package sj.messenger.domain.groupchat.repository

import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import sj.messenger.domain.groupchat.domain.Participant
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.util.assertEntityLoaded
import sj.messenger.util.generateChatRoom
import sj.messenger.util.generateUser
import sj.messenger.util.repository.JpaRepositoryTest

@JpaRepositoryTest
class ParticipantRepositoryTest (
    @Autowired val participantRepository: ParticipantRepository,
    @Autowired val userRepository: UserRepository,
    @Autowired val groupChatRepository: GroupChatRepository,
    @Autowired val em: EntityManager
){

    @Test
    @DisplayName("getParticipantsByUserId 호출시 chatRoom 또한 같이 조회되어야 한다.")
    fun findByUserIdTest(){
        // given
        val user = generateUser()
        userRepository.save(user)

        for(num in 1..10){
            val chatRoom = generateChatRoom()
            groupChatRepository.save(chatRoom)
            participantRepository.save(Participant(user,chatRoom))
        }

        // when
        val result = participantRepository.getParticipantsByUserId(user.id!!);
        val chatRooms = result.map { it.groupChat }
        // then
        Assertions.assertThat(result.size).isEqualTo(10)
        assertEntityLoaded(em, *chatRooms.toTypedArray())
    }
}