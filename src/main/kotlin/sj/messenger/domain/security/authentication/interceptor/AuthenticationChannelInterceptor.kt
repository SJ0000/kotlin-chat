package sj.messenger.domain.security.authentication.interceptor

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.security.authentication.AuthenticationManager
import sj.messenger.domain.security.authentication.JwtPreAuthenticationToken
import sj.messenger.global.extractBearerToken

private val logger = KotlinLogging.logger { }

class AuthenticationChannelInterceptor(
    private val authenticationManager: AuthenticationManager
) : ChannelInterceptor {

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*> {
        return try {
            val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)
                ?: throw NullPointerException("MessageHeaderAccessor를 가져올 수 없습니다.")
            if (accessor.command == StompCommand.CONNECT) {
                val accessToken = extractBearerToken(accessor)
                    ?: throw IllegalArgumentException("Bearer 토큰을 가져오는 데 실패하였습니다.")
                val authentication = authenticationManager.authenticate(JwtPreAuthenticationToken(accessToken))
                accessor.setUser(authentication)
            }
            message
        } catch (e: RuntimeException) {
            logger.error(e) { "STOMP 연결 인증 실패. message = ${e.message}" }
            throw e
        }
    }
}