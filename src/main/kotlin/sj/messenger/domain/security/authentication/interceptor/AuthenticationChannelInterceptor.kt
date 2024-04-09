package sj.messenger.domain.security.authentication.interceptor

import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.security.authentication.AuthenticationManager
import sj.messenger.domain.security.authentication.JwtPreAuthenticationToken

class AuthenticationChannelInterceptor(
    private val authenticationManager: AuthenticationManager
) : ChannelInterceptor {

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*> {
        val accessor = getAccessor(message)
        if (accessor.command == StompCommand.CONNECT) {
            val accessToken = getAuthorizationHeader(accessor)
            val authentication = authenticationManager.authenticate(JwtPreAuthenticationToken(accessToken))
            accessor.setUser(authentication)
        }
        return message
    }

    private fun getAccessor(message: Message<*>): StompHeaderAccessor {
        return MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)
            ?: throw RuntimeException("MessageHeaderAccessor is null")
    }

    private fun getAuthorizationHeader(accessor: StompHeaderAccessor): String {
        return accessor.getFirstNativeHeader("Authorization")
            ?: throw RuntimeException("STOMP Authorization Header is empty");
    }
}