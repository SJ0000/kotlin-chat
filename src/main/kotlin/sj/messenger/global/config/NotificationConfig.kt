package sj.messenger.global.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import sj.messenger.domain.notification.interceptor.NotificationInterceptor
import sj.messenger.domain.notification.service.NotificationService

@Configuration
class NotificationConfig {
    @Bean
    fun notificationInterceptor(
        notificationService: NotificationService,
        objectMapper: ObjectMapper
    ): NotificationInterceptor {
        return NotificationInterceptor(notificationService, objectMapper)
    }
}