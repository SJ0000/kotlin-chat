package sj.messenger.domain.groupchat.repository

import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.util.assertEntityLoaded
import sj.messenger.util.generateChatRoom
import sj.messenger.util.generateUser
import sj.messenger.util.repository.JpaRepositoryTest

@JpaRepositoryTest
class GroupChatRepositoryTest (
    @Autowired private val participantRepository: ParticipantRepository,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val groupChatRepository: GroupChatRepository,

    @Autowired private val em: EntityManager
){

    @Test
    @DisplayName("findWithParticipantsById 호출시 participants 또한 같이 조회되어야 한다.")
    fun findWithParticipantsByIdTest(){
        // given
        val users = (1..3).map { generateUser() }
        userRepository.saveAll(users)
        val chatRoom = generateChatRoom()
        groupChatRepository.save(chatRoom)
        users.forEach{
         chatRoom.join(it)
        }

        em.flush()
        em.clear()

        // when
        val findChatRoom = groupChatRepository.findWithParticipantsById(chatRoom.id!!)
        findChatRoom!!

        // then
        Assertions.assertThat(findChatRoom.participants.size).isEqualTo(3)
        assertEntityLoaded(em,*findChatRoom.participants.toTypedArray())
    }
}