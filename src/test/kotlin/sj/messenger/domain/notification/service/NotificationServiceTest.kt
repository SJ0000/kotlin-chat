package sj.messenger.domain.notification.service

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import sj.messenger.domain.notification.domain.NotificationToken
import sj.messenger.domain.notification.repository.NotificationTokenRepository
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.util.annotation.ServiceTest
import sj.messenger.util.generateUser
import sj.messenger.util.randomString

@ServiceTest
class NotificationServiceTest(
    @Autowired val notificationService: NotificationService,
    @Autowired val notificationTokenRepository: NotificationTokenRepository,
    @Autowired val userRepository: UserRepository,
){

    @Test
    @DisplayName("사용자의 알림 토큰이 존재하지 않는 경우에만 토큰을 생성할 수 있다.")
    fun createTokenIfNotExistsTest() {
        // given
        val user = userRepository.save(generateUser())
        val token = randomString(255)

        // when
        notificationService.createTokenIfNotExists(user.id!!, token)

        // then
        val tokens = notificationTokenRepository.findAll()
        assertThat(tokens.size).isEqualTo(1)
        assertThat(tokens[0].user).isEqualTo(user)
        assertThat(tokens[0].fcmToken).isEqualTo(token)
    }

    @Test
    @DisplayName("사용자의 알림 토큰이 이미 존재할 경우 생성시 예외 발생")
    fun createTokenIfNotExistsError() {
        // given
        val user = userRepository.save(generateUser())
        notificationTokenRepository.save(NotificationToken(user,randomString(255)))

        // expected
        Assertions.assertThatThrownBy {
            notificationService.createTokenIfNotExists(user.id!!, randomString(100))
        }.isInstanceOf(RuntimeException::class.java)
    }

    @Test
    @DisplayName("토큰 갱신 성공")
    fun updateNotificationToken() {
        // given
        val user = userRepository.save(generateUser())
        notificationTokenRepository.save(NotificationToken(user,randomString(255)))

        // when
        val newFcmToken = randomString(100)
        notificationService.updateNotificationToken(user.id!!,newFcmToken)

        // then
        val token = notificationTokenRepository.findFirstByUserId(user.id!!)
        assertThat(token?.fcmToken).isEqualTo(newFcmToken)
    }

    @Test
    @DisplayName("토큰 갱신시 갱신할 토큰이 없는 경우 예외 발생")
    fun updateNotificationTokenNotFound() {
        // given
        val user = userRepository.save(generateUser())

        // when
        assertThatThrownBy {
            notificationService.updateNotificationToken(user.id!!, randomString(100))
        }.isInstanceOf(RuntimeException::class.java)
    }

    @Test
    @DisplayName("사용자의 notification token을 삭제한다.")
    fun removeUserNotificationTokenTest() {
        // given
        val user = userRepository.save(generateUser())
        notificationTokenRepository.save(NotificationToken(user,randomString(255)))

        // when
        notificationService.removeUserNotificationToken(user.id!!)

        // then
        val exists = notificationTokenRepository.existsByUserId(user.id!!)
        assertThat(exists).isFalse()
    }
}