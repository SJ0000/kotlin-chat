package sj.messenger.global.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.validation.annotation.Validated
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
@EnableConfigurationProperties(WebSocketProperties::class)
class WebSocketConfig(
    private val properties: WebSocketProperties,
    // private val authenticationManager: AuthenticationManager,
) : WebSocketMessageBrokerConfigurer{

    @Value("\${client.url}")
    lateinit var clientUrl : String

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableSimpleBroker("/topic")
        registry.setApplicationDestinationPrefixes("/app")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/message-broker").setAllowedOriginPatterns(clientUrl, "http://localhost:[*]")
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
//        val authenticationInterceptor = AuthenticationChannelInterceptor(authenticationManager)
//        registration.interceptors(authenticationInterceptor)
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = properties.corePoolSize
        executor.setAllowCoreThreadTimeOut(true)
        registration.taskExecutor(executor)
    }

    override fun configureClientOutboundChannel(registration: ChannelRegistration) {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = properties.corePoolSize
        executor.setAllowCoreThreadTimeOut(true)
        registration.taskExecutor(executor)
    }
}

@Validated
@ConfigurationProperties("websocket")
data class WebSocketProperties(
    val corePoolSize: Int
)