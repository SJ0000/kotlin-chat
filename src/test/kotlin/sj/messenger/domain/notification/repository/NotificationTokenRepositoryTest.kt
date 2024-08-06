package sj.messenger.domain.notification.repository

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
    fun findAllByUserIdTest() {
        // given
        val user = userRepository.save(generateUser())
        tokenRepository.saveAll((1..3).map {
            NotificationToken(user, randomString(150))
        })

        // when
        val result = tokenRepository.findAllByUserId(user.id!!)

        // then
        assertThat(result.size).isEqualTo(3)
    }

    @Test
    @DisplayName("사용자의 NotificationToken 존재할 경우 true")
    fun existsByUserIdExists() {
        // given
        val user = userRepository.save(generateUser())
        tokenRepository.save(NotificationToken(user, randomString(150)))

        // when
        val exists = tokenRepository.existsByUserId(user.id!!)

        // then
        assertThat(exists).isTrue()
    }

    @Test
    @DisplayName("사용자의 NotificationToken 존재하지 않을 경우 false")
    fun existsByUserIdNotExists() {
        // given
        val user = userRepository.save(generateUser())

        // when
        val exists = tokenRepository.existsByUserId(user.id!!)

        // then
        assertThat(exists).isFalse()
    }

    @Test
    @DisplayName("사용자의 NotificationToken 단일 조회")
    fun findFirstByUserIdTest() {
        // given
        val user = userRepository.save(generateUser())
        tokenRepository.save(NotificationToken(user, randomString(150)))
        // when
        val token = tokenRepository.findFirstByUserId(user.id!!)
        // then
        assertThat(token).isNotNull
        assertThat(token?.user?.id).isEqualTo(user.id)
    }

    @Test
    @DisplayName("사용자의 NotificationToken 단일 조회시 없을 경우 null 반환")
    fun findFirstByUserIdNull() {
        // given
        val user = userRepository.save(generateUser())
        // when
        val token = tokenRepository.findFirstByUserId(user.id!!)
        // then
        assertThat(token).isNull()
    }

    @Test
    @DisplayName("여러 사용자의 NotificationToken 목록을 조회한다.")
    fun findAllByUserIdsTest() {
        // given
        val users = userRepository.saveAll((1..3).map { generateUser() })

        users.forEach { user ->
            val tokens = (1..3).map { NotificationToken(user, randomString(150)) }
            tokenRepository.saveAll(tokens)
        }

        // when
        val userIds = users.map { it.id!! }
        val result = tokenRepository.findAllByUserIds(userIds)

        // then
        assertThat(result.size).isEqualTo(9)
    }
}