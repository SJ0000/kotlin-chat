package sj.messenger.domain.notification.service

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.MulticastMessage
import com.google.firebase.messaging.Notification
import org.springframework.stereotype.Service

/**
 * FirebaseConfig에서 생성되는 bean
 */
class FirebaseMessagingService(
    private val firebaseMessaging: FirebaseMessaging,
) : NotificationMessagingService {

    override fun sendMessageAsync(title: String, content: String, targets: List<String>){
        val fcmMessage = createFcmMessage(title, content, targets)
        firebaseMessaging.sendEachForMulticastAsync(fcmMessage)
    }

    private fun createNotification(title: String, body: String): Notification {
        return Notification.builder()
            .setTitle(title)
            .setBody(body)
            .build();
    }

    private fun createFcmMessage(title: String, content: String, fcmTokens: List<String>): MulticastMessage {
        if (fcmTokens.isEmpty())
            throw IllegalArgumentException("fcmTokens가 비어있습니다.")

        return MulticastMessage.builder()
            .setNotification(createNotification(title, content))
            .addAllTokens(fcmTokens)
            .build()
    }
}