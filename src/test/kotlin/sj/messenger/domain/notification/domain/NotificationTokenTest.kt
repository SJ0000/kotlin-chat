package sj.messenger.domain.notification.domain

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.test.util.ReflectionTestUtils
import sj.messenger.util.generateUser
import sj.messenger.util.randomString
import java.time.LocalDateTime

class NotificationTokenTest {

    @Test
    fun isExpiredTrue() {
        val notificationToken = NotificationToken(generateUser(), randomString(100))
        ReflectionTestUtils.setField(notificationToken, "modifiedAt", LocalDateTime.now().minusDays(61))
        assertThat(notificationToken.isExpired()).isTrue()
    }

    @Test
    fun isExpiredFalse() {
        val notificationToken = NotificationToken(generateUser(), randomString(100))
        ReflectionTestUtils.setField(notificationToken, "modifiedAt", LocalDateTime.now().minusDays(59))
        assertThat(notificationToken.isExpired()).isFalse()
    }
}