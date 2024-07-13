package sj.messenger.domain.groupchat.repository

import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.util.annotation.JpaRepositoryTest
import sj.messenger.util.assertEntityLoaded
import sj.messenger.util.generateGroupChat
import sj.messenger.util.generateUser

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
            val chatRoom = generateGroupChat(user)
            groupChatRepository.save(chatRoom)
        }

        // when
        val result = participantRepository.getParticipantsWithGroupChatByUserId(user.id!!);
        val chatRooms = result.map { it.groupChat }
        // then
        assertThat(result.size).isEqualTo(10)
        assertEntityLoaded(em, *chatRooms.toTypedArray())
    }

    @Test
    @DisplayName("groupChatId와 userId로 participant 조회")
    fun findByGroupChatIdAndUserIdTest(){
        // given
        val user = userRepository.save(generateUser())
        val groupChat = groupChatRepository.save(generateGroupChat(user))

        // when
        val participant = participantRepository.findByGroupChatIdAndUserId(groupChat.id!!, user.id!!);

        // then
        assertThat(participant.user).isEqualTo(user)
        assertThat(participant.groupChat).isEqualTo(groupChat)

    }
}