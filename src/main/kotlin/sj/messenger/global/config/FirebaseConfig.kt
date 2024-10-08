package sj.messenger.global.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import sj.messenger.domain.notification.service.FirebaseMessagingService
import sj.messenger.domain.notification.service.NotificationMessagingService

@Configuration
class FirebaseConfig(
    @Value("\${app.firebase.secret-path}")
    val firebaseSecretPath: String,
) {

    @Bean
    fun firebaseMessagingService() : NotificationMessagingService{
        initializeFirebase()
        return FirebaseMessagingService(FirebaseMessaging.getInstance())
    }

    fun initializeFirebase() {
        val credentials = getFirebaseCredentials();
        val options = FirebaseOptions.builder()
            .setCredentials(credentials)
            .build()
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options)
        }
    }

    private fun getFirebaseCredentials(): GoogleCredentials {
        val fcmSecretInputStream = ClassPathResource(firebaseSecretPath).inputStream
        return GoogleCredentials.fromStream(fcmSecretInputStream)
    }
}