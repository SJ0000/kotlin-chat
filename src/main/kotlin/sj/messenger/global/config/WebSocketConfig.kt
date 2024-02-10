package sj.messenger.global.config

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import sj.messenger.domain.security.authentication.interceptor.AuthenticationChannelInterceptor

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig(
    private val authenticationManager: AuthenticationManager
) : WebSocketMessageBrokerConfigurer{

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableSimpleBroker("/topic")
        registry.setApplicationDestinationPrefixes("/app")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/message-broker").setAllowedOrigins("http://localhost:3000")
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        val authenticationInterceptor = AuthenticationChannelInterceptor(authenticationManager)
        registration.interceptors(authenticationInterceptor)
    }
}