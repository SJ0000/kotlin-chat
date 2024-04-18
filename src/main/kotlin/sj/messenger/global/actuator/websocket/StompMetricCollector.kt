package sj.messenger.global.actuator.websocket

import org.springframework.web.socket.config.WebSocketMessageBrokerStats

abstract class StompMetricCollector(
    protected val source: WebSocketMessageBrokerStats
) {
    abstract fun collectWebSocketSessionMetric(): WebSocketSessionMetric;
    abstract fun collectInboundChannelMetric(): ThreadPoolMetric;
    abstract fun collectOutboundChannelMetric(): ThreadPoolMetric;
}