package sj.messenger.domain.notification.domain

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.test.util.ReflectionTestUtils
import sj.messenger.util.generateUser
import sj.messenger.util.randomString
import java.time.LocalDateTime

class NotificationTokenTest {

    @Test
    fun isModifiedOverTrue() {
        val notificationToken = NotificationToken(generateUser(), randomString(100))
        ReflectionTestUtils.setField(notificationToken, "modifiedAt", LocalDateTime.now().minusDays(61))
        assertThat(notificationToken.isModifiedAfter(60)).isTrue()
    }

    @Test
    fun isModifiedOverFalse() {
        val notificationToken = NotificationToken(generateUser(), randomString(100))
        ReflectionTestUtils.setField(notificationToken, "modifiedAt", LocalDateTime.now().minusDays(59))
        assertThat(notificationToken.isModifiedAfter(60)).isFalse()
    }
}