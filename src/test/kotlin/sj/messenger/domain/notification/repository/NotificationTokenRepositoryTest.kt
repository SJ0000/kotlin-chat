package sj.messenger.domain.notification.repository

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import sj.messenger.domain.notification.domain.NotificationToken
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.util.annotation.JpaRepositoryTest
import sj.messenger.util.generateUser
import sj.messenger.util.randomString

@JpaRepositoryTest
class NotificationTokenRepositoryTest(
    @Autowired val userRepository: UserRepository,
    @Autowired val tokenRepository: NotificationTokenRepository
) {
    @Test
    @DisplayName("사용자의 NotificationToken 목록을 조회한다.")
    fun findByUserIdTest() {
        // given
        val user = userRepository.save(generateUser())
        tokenRepository.saveAll((1..3).map {
            NotificationToken(user, randomString(150))
        })

        // when
        val result = tokenRepository.findByUserId(user.id!!)

        // then
        assertThat(result.size).isEqualTo(3)

    }
}