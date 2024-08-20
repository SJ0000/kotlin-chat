package sj.messenger.domain.notification.service


import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.MulticastMessage
import com.google.firebase.messaging.Notification
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.BDDMockito
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.util.ReflectionTestUtils
import sj.messenger.util.randomString


@ExtendWith(MockitoExtension::class)
class FirebaseMessagingServiceTest{

    @Mock lateinit var  firebaseMessaging: FirebaseMessaging
    @InjectMocks lateinit var firebaseMessagingService: FirebaseMessagingService
    @Captor lateinit var argumentCaptor: ArgumentCaptor<MulticastMessage>
    
    @Test
    @DisplayName("FCM 메시지 전송 파라미터 검증 테스트")
    fun sendMessageAsyncTest() {
        // given
        val title = randomString(10)
        val content = randomString(20)
        val targets = (1..3).map { randomString(30) }

        // when
        firebaseMessagingService.sendMessageAsync(title,content,targets)
        Mockito.verify(firebaseMessaging).sendEachForMulticastAsync(argumentCaptor.capture())

        // then
        val multicastMessage = argumentCaptor.value!!
        val tokens = ReflectionTestUtils.getField(multicastMessage, "tokens") as List<*>
        val notification = ReflectionTestUtils.getField(multicastMessage, "notification") as Notification
        val notificationTitle = ReflectionTestUtils.getField(notification, "title") as String
        val notificationBody = ReflectionTestUtils.getField(notification, "body") as String

        assertThat(tokens).containsAll(targets)
        assertThat(notificationTitle).isEqualTo(title)
        assertThat(notificationBody).isEqualTo(content)
    }

    @Test
    @DisplayName("인자로 전달된 targets 비어있는 경우 RuntimeException 반환")
    fun sendMessageAsyncErrorTest() {
        assertThatThrownBy {
            firebaseMessagingService.sendMessageAsync(randomString(10), randomString(10), listOf())
        }.isInstanceOf(RuntimeException::class.java)
    }
}