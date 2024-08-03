package sj.messenger.domain.notification.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
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
    @DisplayName("알림 토큰 생성 성공")
    fun createTokenTest() {
        // given
        val user = userRepository.save(generateUser())
        val token = randomString(255)

        // when
        notificationService.createToken(user.id!!, token)

        // then
        val tokens = notificationTokenRepository.findAll()
        assertThat(tokens.size).isEqualTo(1)
        assertThat(tokens[0].user).isEqualTo(user)
        assertThat(tokens[0].fcmToken).isEqualTo(token)
    }
}