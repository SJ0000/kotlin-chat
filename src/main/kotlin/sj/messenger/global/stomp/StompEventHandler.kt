package sj.messenger.global.stomp

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionConnectedEvent

private val logger = KotlinLogging.logger {  }

@Component
class StompEventHandler {
    @EventListener
    fun eventHandler(e: SessionConnectedEvent){
        logger.info { "사용자 ${e.user?.name} 가 채팅 서버에 접속하였습니다." }
    }
}