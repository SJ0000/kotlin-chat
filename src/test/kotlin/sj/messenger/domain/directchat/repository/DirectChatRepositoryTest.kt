package sj.messenger.domain.directchat.repository

import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import sj.messenger.JpaRepositoryTest
import sj.messenger.domain.directchat.domain.DirectChat
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.util.assertEntityLoaded
import sj.messenger.util.generateUser

@JpaRepositoryTest
class DirectChatRepositoryTest (
    @Autowired val directChatRepository: DirectChatRepository,
    @Autowired val userRepository: UserRepository,
    @Autowired val em: EntityManager,
){
    @Test
    @DisplayName("사용자의 DirectChat 목록 조회시 user를 같이 조회한다. ")
    fun findAllByUserIdWithUsersTest(){
        // given
        val user = generateUser()
        val users = (1..3).map { generateUser() }
        userRepository.save(user)
        userRepository.saveAll(users)
        directChatRepository.saveAll(users.map { DirectChat(it,user) })

        em.flush()
        em.clear()
        // when
        val directChats = directChatRepository.findAllByUserIdWithUsers(user.id!!)
        val directChatsUsers = directChats.map { it.getUsers() }.flatten()
        // then
        assertThat(directChats.size).isEqualTo(users.size)
        assertEntityLoaded(em, *directChatsUsers.toTypedArray())
    }

    @Test
    @DisplayName("DirectChat 조회시 user를 같이 조회한다.")
    fun findByIdWithUsers(){
        // given
        val users = (1..2).map { generateUser() }
        userRepository.saveAll(users)
        val directChat = directChatRepository.save(DirectChat(users[0], users[1]))
        em.flush()
        em.clear()

        // when
        val result = directChatRepository.findByIdWithUsers(directChat.id!!)

        // then
        result!!
        assertEntityLoaded(em, *result.getUsers().toTypedArray())
    }
}