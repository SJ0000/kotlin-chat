package sj.messenger.domain.notification.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.navercorp.fixturemonkey.kotlin.giveMeOne
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import sj.messenger.domain.notification.dto.NotificationTokenCreate
import sj.messenger.domain.notification.repository.NotificationTokenRepository
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.util.annotation.IntegrationTest
import sj.messenger.util.config.InjectAccessToken
import sj.messenger.util.fixture

@IntegrationTest
class NotificationControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val om : ObjectMapper,
    @Autowired val userRepository: UserRepository,
    @Autowired val notificationTokenRepository: NotificationTokenRepository,
){

    @Test
    @DisplayName("POST /notifications/tokens : 정상 동작시 201 Created")
    @InjectAccessToken
    fun postNotificationToken(){
        // given
        val tokenCreate : NotificationTokenCreate = fixture.giveMeOne();

        // expected
        mockMvc.post("/notifications/tokens"){
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
    fun postNotificationTokenEmpty(){
        // given
        val emptyToken = NotificationTokenCreate("")

        // expected
        mockMvc.post("/notifications/tokens"){
            contentType = MediaType.APPLICATION_JSON
            content = om.writeValueAsString(emptyToken)
        }.andExpect {
            status { isBadRequest() }
        }
    }
}