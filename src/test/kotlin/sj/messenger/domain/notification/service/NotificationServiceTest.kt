package sj.messenger.domain.notification.service

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.test.util.ReflectionTestUtils
import sj.messenger.domain.notification.domain.NotificationToken
import sj.messenger.domain.notification.repository.NotificationTokenRepository
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.global.exception.ExpiredFcmTokenException
import sj.messenger.global.exception.FcmTokenAlreadyExistsException
import sj.messenger.util.annotation.ServiceTest
import sj.messenger.util.generateUser
import sj.messenger.util.randomString
import java.time.LocalDate
import java.time.LocalDateTime

@ServiceTest
class NotificationServiceTest(
    @Autowired val notificationService: NotificationService,
    @Autowired val notificationTokenRepository: NotificationTokenRepository,
    @Autowired val userRepository: UserRepository,
    @Value("\${app.firebase.fcm.token-expiration-days}") val fcmTokenExpirationDays: Long,
) {

    @Test
    @DisplayName("사용자의 알림 토큰이 존재하지 않는 경우에만 토큰을 등록할 수 있다.")
    fun registerFcmTokenTest() {
        // given
        val user = userRepository.save(generateUser())
        val token = randomString(255)

        // when
        notificationService.registerFcmToken(user.id!!, token)

        // then
        val tokens = notificationTokenRepository.findAll()
        assertThat(tokens.size).isEqualTo(1)
        assertThat(tokens[0].user).isEqualTo(user)
        assertThat(tokens[0].fcmToken).isEqualTo(token)
    }

    @Test
    @DisplayName("신규 토큰 등록시 등록된 다른 토큰이 있다면 FcmTokenAlreadyExistsException 발생")
    fun registerFcmTokenAlreadyExists() {
        // given
        val user = userRepository.save(generateUser())
        notificationTokenRepository.save(NotificationToken(user, randomString(255)))

        // expected
        assertThatThrownBy {
            notificationService.registerFcmToken(user.id!!, randomString(100))
        }.isInstanceOf(FcmTokenAlreadyExistsException::class.java)
    }

    @Test
    @DisplayName("기존 토큰 등록시 유효기간이 만료한 경우 ExpiredFcmTokenException 발생")
    fun registerFcmTokenExpired() {
        // given
        val user = userRepository.save(generateUser())
        val notificationToken = NotificationToken(user, randomString(255))
        notificationTokenRepository.save(notificationToken)
        ReflectionTestUtils.setField(
            notificationToken,
            "modifiedAt",
            LocalDateTime.now().minusDays(fcmTokenExpirationDays + 1)
        )

        // expected
        assertThatThrownBy {
            notificationService.registerFcmToken(user.id!!, notificationToken.fcmToken)
        }.isInstanceOf(ExpiredFcmTokenException::class.java)
    }


    @Test
    @DisplayName("토큰 갱신 성공")
    fun updateNotificationToken() {
        // given
        val user = userRepository.save(generateUser())
        notificationTokenRepository.save(NotificationToken(user, randomString(255)))

        // when
        val newFcmToken = randomString(100)
        notificationService.updateNotificationToken(user.id!!, newFcmToken)

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
        notificationTokenRepository.save(NotificationToken(user, randomString(255)))

        // when
        notificationService.removeUserNotificationToken(user.id!!)

        // then
        val exists = notificationTokenRepository.existsByUserId(user.id!!)
        assertThat(exists).isFalse()
    }
}