package sj.messenger.domain.directchat.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import sj.messenger.domain.directchat.domain.DirectChat
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.util.generateUser
import sj.messenger.util.repository.JpaRepositoryTest

@JpaRepositoryTest
class DirectChatRepositoryImplTest(
    @Autowired val directChatRepository: DirectChatRepository,
    @Autowired val userRepository: UserRepository,
) {

    @Test
    @DisplayName("existsByUserIds()는 사용자 2명의 DirectChat이 존재하는 경우 true를 반환한다.")
    fun existsByUserIdsTest() {
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

    @Test
    @DisplayName("existsByUserIds()는 사용자 2명의 DirectChat이 존재하지 않는 경우 false를 반환한다.")
    fun notExistsByUserIdsTest() {
        // when
        val result = directChatRepository.existsByUserIds(Pair(1L,2L))

        // then
        assertThat(result).isFalse()
    }
}