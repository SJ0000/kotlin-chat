package sj.messenger.domain.notification.domain

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import sj.messenger.util.generateUser
import sj.messenger.util.randomString
import java.time.LocalDateTime

class NotificationTokenTest {

    @Test
    fun isExpiredTrue() {
        val notificationToken = NotificationToken(generateUser(), randomString(100))
        assertThat(notificationToken.isExpired(LocalDateTime.now().plusDays(61))).isTrue()
    }

    @Test
    fun isExpiredFalse() {
        val notificationToken = NotificationToken(generateUser(), randomString(100))
        assertThat(notificationToken.isExpired(LocalDateTime.now().plusDays(59))).isFalse()
    }
}