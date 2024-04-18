package sj.messenger.global.config

import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.binder.MeterBinder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.WebSocketMessageBrokerStats
import sj.messenger.global.actuator.websocket.ReflectionStompStatsAdapter
import sj.messenger.global.actuator.websocket.StompStatsProvider
import sj.messenger.global.actuator.websocket.WebSocketSessionStats

@Configuration
class MetricConfig {
    /**
     * Micrometer는 약한 참조를 사용하기 때문에 metric target 객체가 GC에 의해 제거되면
     * 데이터를 수집할 수 없음
     * 따라서 Bean으로 등록해서 수집되지 않게 함
     *
     * 참고 : https://stackoverflow.com/questions/56889240/why-do-reading-micrometer-measurement-returns-nan-sometimes
     */
    @Bean
    fun websocketSessionStats(source : WebSocketMessageBrokerStats): WebSocketSessionStats{
        val provider: StompStatsProvider = ReflectionStompStatsAdapter(source)
        return provider.getWebSocketSessionStats()
    }

    @Bean
    fun stompMetrics(stats: WebSocketSessionStats) : MeterBinder{
        val prefix = "stomp.websocket"
        return MeterBinder { registry ->
            Gauge.builder("${prefix}.current-websockets", stats) {
                it.currentWebSockets.toDouble()
            }.register(registry)
            Gauge.builder("${prefix}.total-sessions", stats) {
                it.totalSessions.toDouble()
            }.register(registry)
            Gauge.builder("${prefix}.connection-failure", stats) {
                it.connectFailure.toDouble()
            }.register(registry)
            Gauge.builder("${prefix}.send-limit", stats) {
                it.sendLimit.toDouble()
            }.register(registry)
            Gauge.builder("${prefix}.transport-error", stats) {
                it.transportError.toDouble()
            }.register(registry)
        }
    }
}