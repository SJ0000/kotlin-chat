package sj.messenger.domain.notification.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.navercorp.fixturemonkey.kotlin.giveMeOne
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import sj.messenger.domain.notification.domain.NotificationToken
import sj.messenger.domain.notification.dto.NotificationTokenCreate
import sj.messenger.domain.notification.dto.NotificationTokenUpdate
import sj.messenger.domain.notification.repository.NotificationTokenRepository
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.util.annotation.IntegrationTest
import sj.messenger.util.config.InjectAccessToken
import sj.messenger.util.fixture
import sj.messenger.util.randomString

@IntegrationTest
class NotificationControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val om: ObjectMapper,
    @Autowired val userRepository: UserRepository,
    @Autowired val notificationTokenRepository: NotificationTokenRepository,
) {

    @Test
    @DisplayName("POST /notifications/tokens : 정상 동작시 201 Created")
    @InjectAccessToken
    fun postNotificationToken() {
        // given
        val tokenCreate: NotificationTokenCreate = fixture.giveMeOne();

        // expected
        mockMvc.post("/notifications/tokens") {
            contentType = MediaType.APPLICATION_JSON
            content = om.writeValueAsString(tokenCreate)
        }.andExpect {
            status { isCreated() }
        }

        val user = userRepository.findByEmail("test@test.com")!!
        val token = notificationTokenRepository.findAll()[0]
        assertThat(token).isNotNull
        assertThat(token.user.id).isEqualTo(user.id)
        assertThat(token.fcmToken).isEqualTo(tokenCreate.fcmToken)
    }

    @Test
    @DisplayName("POST /notifications/tokens : FCM 토큰이 비어있을 경우 400 bad request")
    @InjectAccessToken
    fun postNotificationTokenEmpty() {
        // given
        val emptyToken = NotificationTokenCreate("")

        // expected
        mockMvc.post("/notifications/tokens") {
            contentType = MediaType.APPLICATION_JSON
            content = om.writeValueAsString(emptyToken)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    @DisplayName("PATCH /notifications/tokens : FCM 토큰 갱신")
    @InjectAccessToken
    fun patchNotificationToken() {
        // given
        val user = userRepository.findAll()[0]
        notificationTokenRepository.save(NotificationToken(user, randomString(255)))

        val newFcmToken = randomString(255)
        val dto = NotificationTokenUpdate(newFcmToken)

        // expected
        mockMvc.patch("/notifications/tokens") {
            contentType = MediaType.APPLICATION_JSON
            content = om.writeValueAsString(dto)
        }.andExpect {
            status { isOk() }
        }

        val updatedToken = notificationTokenRepository.findFirstByUserId(user.id!!)
        assertThat(updatedToken?.fcmToken).isEqualTo(newFcmToken)
    }

    @Test
    @DisplayName("PATCH /notifications/tokens : FCM 토큰이 비어있을 경우 400 bad request")
    @InjectAccessToken
    fun patchNotificationTokenEmpty() {
        // given
        val user = userRepository.findAll()[0]
        val emptyToken = NotificationTokenCreate("")

        // expected
        mockMvc.patch("/notifications/tokens") {
            contentType = MediaType.APPLICATION_JSON
            content = om.writeValueAsString(emptyToken)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    @DisplayName("DELETE /notifications/tokens : FCM 토큰 삭제 후 204 No Content")
    @InjectAccessToken
    fun deleteNotificationToken() {
        // given
        val user = userRepository.findAll()[0]
        notificationTokenRepository.save(NotificationToken(user, randomString(255)))

        // expected
        mockMvc.delete("/notifications/tokens")
            .andExpect {
                status { isNoContent() }
            }

        val result = notificationTokenRepository.findFirstByUserId(user.id!!)
        assertThat(result).isNull()
    }
}