package sj.messenger.domain.directchat.service

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import sj.messenger.domain.directchat.domain.DirectChat
import sj.messenger.domain.directchat.repository.DirectChatRepository
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.util.generateUser
import sj.messenger.util.integration.EnableContainers

@SpringBootTest
@EnableContainers
@Transactional
class DirectGroupChatServiceTest(
    @Autowired val directChatService: DirectChatService,
    @Autowired val userRepository: UserRepository,
    @Autowired val directChatRepository: DirectChatRepository,
) {

    @Test
    @DisplayName("자신이 속해있는 DirectChat을 조회")
    fun getDirectChat() {
        // given
        val users = (1..2).map { generateUser() }
        userRepository.saveAll(users)
        val directChat = directChatRepository.save(DirectChat(users[0], users[1]))

        // when
        val result = directChatService.getDirectChat(users[0].id!!, directChat.id!!)

        // then
        assertThat(result.hasAuthority(users[0].id!!)).isTrue()
    }

    @Test
    @DisplayName("자신이 속해있지 않은 DirectChat을 조회시 예외 발생")
    fun getDirectChatError() {
        // given
        val users = (1..2).map { generateUser() }
        val other = generateUser()
        userRepository.saveAll(users + listOf(other))
        val directChat = directChatRepository.save(DirectChat(users[0], users[1]))

        // expected
        assertThatThrownBy {
            directChatService.getDirectChat(other.id!!, directChat.id!!)
        }.isInstanceOf(RuntimeException::class.java)
    }

    @Test
    @DisplayName("사용자 2명의 DirectChat 생성")
    fun createDirectChat() {
        // given
        val users = (1..2).map { generateUser() }
        userRepository.saveAll(users)

        // when
        val directChatId = directChatService.createDirectChat(Pair(users[0].id!!, users[1].id!!))

        // then
        val directChat = directChatRepository.findByIdOrNull(directChatId)
        assertThat(directChat).isNotNull
        assertThat(directChat?.getUsers()?.map { it.id!! }).containsAll(users.map { it.id!! })
    }

    @Test
    @DisplayName("같은 사용자 2명의 DirectChat은 1개 이상 존재할 수 없다.")
    fun createDirectChatError() {
        // given
        val users = (1..2).map { generateUser() }
        userRepository.saveAll(users)
        directChatRepository.save(DirectChat(users[0],users[1]))

        // expected
        assertThatThrownBy {
            directChatService.createDirectChat(Pair(users[0].id!!, users[1].id!!))
        }.isInstanceOf(RuntimeException::class.java)
    }
}