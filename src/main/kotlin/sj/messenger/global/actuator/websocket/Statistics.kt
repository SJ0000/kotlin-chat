package sj.messenger.global.actuator.websocket

import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler

class WebSocketSessionStats(
    private val source: SubProtocolWebSocketHandler.Stats
) {
    var currentWebSockets: Int = 0
        get() = source.webSocketSessions
        private set
    var totalSessions: Int = 0
        get() = source.totalSessions
        private set
    var connectFailure: Int = 0
        get() = source.noMessagesReceivedSessions
        private set
    var sendLimit: Int = 0
        get() = source.limitExceededSessions
        private set
    var transportError: Int = 0
        get() = source.transportErrorSessions
        private set
}