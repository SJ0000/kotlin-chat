package sj.messenger.global.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.validation.annotation.Validated
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import sj.messenger.domain.notification.interceptor.NotificationInterceptor

@Configuration(proxyBeanMethods = false)
@EnableWebSocketMessageBroker
@EnableConfigurationProperties(WebSocketProperties::class)
class WebSocketConfig(
    private val properties: WebSocketProperties,
    private val notificationInterceptor: NotificationInterceptor,
) : WebSocketMessageBrokerConfigurer{

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableSimpleBroker("/topic")
        registry.setApplicationDestinationPrefixes("/app")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/message-broker").setAllowedOriginPatterns(properties.clientUrl, "http://localhost:[*]")
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = properties.channelPoolSize.inbound
        executor.setAllowCoreThreadTimeOut(true)
        registration.taskExecutor(executor)
    }

    override fun configureClientOutboundChannel(registration: ChannelRegistration) {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = properties.channelPoolSize.outbound
        executor.setAllowCoreThreadTimeOut(true)
        registration.taskExecutor(executor)

        registration.interceptors(notificationInterceptor)
    }
}

@Validated
@ConfigurationProperties("app.websocket")
data class WebSocketProperties(
    val clientUrl: String,
    val channelPoolSize: ChannelPoolSize,
)

data class ChannelPoolSize(
    val inbound: Int,
    val outbound: Int,
)