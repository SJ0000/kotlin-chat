package sj.messenger.domain.directchat.repository

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import sj.messenger.RepositoryTest
import sj.messenger.domain.directchat.domain.DirectChat
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.util.generateUser

@RepositoryTest
class DirectChatRepositoryImplTest(
    @Autowired val directChatRepository: DirectChatRepository,
    @Autowired val userRepository: UserRepository,
) {

    @Test
    fun test() {
        // given
        val users = (1..2).map { generateUser() }
        userRepository.saveAll(users)
        directChatRepository.save(DirectChat(users[0], users[1]))

        // when
        val result1 = directChatRepository.existsByUserIds(Pair(users[0].id!!, users[1].id!!))
        val result2 = directChatRepository.existsByUserIds(Pair(users[1].id!!, users[0].id!!))

        // then
        assertThat(listOf(result1,result2))
            .allMatch { it }

    }

}