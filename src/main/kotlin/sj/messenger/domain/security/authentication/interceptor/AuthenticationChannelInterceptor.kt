package sj.messenger.domain.security.authentication.interceptor

import org.springframework.http.HttpHeaders
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
            ?: throw NullPointerException("MessageHeaderAccessor가 null 입니다.")
    }

    private fun getAuthorizationHeader(accessor: StompHeaderAccessor): String {
        return accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION)
            ?: throw IllegalArgumentException("STOMP Authorization Header가 비어있습니다.");
    }
}