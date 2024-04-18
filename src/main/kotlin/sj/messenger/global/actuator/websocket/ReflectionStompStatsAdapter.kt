package sj.messenger.global.actuator.websocket

import org.springframework.util.ReflectionUtils
import org.springframework.web.socket.config.WebSocketMessageBrokerStats
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler

class ReflectionStompStatsAdapter(
    private val messageBrokerStats: WebSocketMessageBrokerStats
) : StompStatsProvider {

    companion object {
        const val WEBSOCKET_HANDLER_NAME = "webSocketHandler"
    }

    override fun getWebSocketSessionStats(): WebSocketSessionStats {
        val handler = getSourcePrivateField<SubProtocolWebSocketHandler>(WEBSOCKET_HANDLER_NAME)
        return WebSocketSessionStats(handler.stats)
    }

    private inline fun <reified T> getSourcePrivateField(name: String): T {
        val field = ReflectionUtils.findField(WebSocketMessageBrokerStats::class.java, name)!!
        ReflectionUtils.makeAccessible(field)
        return ReflectionUtils.getField(field, messageBrokerStats) as T
    }
}