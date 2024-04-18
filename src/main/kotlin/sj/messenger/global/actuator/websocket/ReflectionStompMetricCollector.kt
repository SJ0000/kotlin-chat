package sj.messenger.global.actuator.websocket

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.util.ReflectionUtils
import org.springframework.web.socket.config.WebSocketMessageBrokerStats
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler

class ReflectionStompMetricCollector(
    messageBrokerStats: WebSocketMessageBrokerStats
) : StompMetricCollector(messageBrokerStats) {

    companion object {
        @JvmStatic
        val WEBSOCKET_HANDLER_NAME = "webSocketHandler"
        @JvmStatic
        val INBOUND_CHANNEL_EXECUTOR_NAME = "inboundChannelExecutor"
        @JvmStatic
        val OUTBOUND_CHANNEL_EXECUTOR_NAME = "outboundChannelExecutor"
    }

    override fun collectWebSocketSessionMetric(): WebSocketSessionMetric {
        val handler = getSourcePrivateField<SubProtocolWebSocketHandler>(WEBSOCKET_HANDLER_NAME)
        return WebSocketSessionMetric(handler.stats)
    }

    override fun collectInboundChannelMetric(): ThreadPoolMetric {
        val executor = getSourcePrivateField<ThreadPoolTaskExecutor>(INBOUND_CHANNEL_EXECUTOR_NAME)
        return ThreadPoolMetric(executor.threadPoolExecutor)
    }

    override fun collectOutboundChannelMetric(): ThreadPoolMetric {
        val executor = getSourcePrivateField<ThreadPoolTaskExecutor>(OUTBOUND_CHANNEL_EXECUTOR_NAME)
        return ThreadPoolMetric(executor.threadPoolExecutor)
    }

    private inline fun <reified T> getSourcePrivateField(name: String): T {
        val field = ReflectionUtils.findField(WebSocketMessageBrokerStats::class.java, name)!!
        ReflectionUtils.makeAccessible(field)
        return ReflectionUtils.getField(field, source) as T
    }
}