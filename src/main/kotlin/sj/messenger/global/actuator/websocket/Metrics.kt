package sj.messenger.global.actuator.websocket

import org.springframework.web.socket.config.WebSocketMessageBrokerStats
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler
import java.util.concurrent.ThreadPoolExecutor


data class WebSocketSessionMetric(
    val currentWebSockets: Int = 0,
    val totalSessions: Int = 0,
    val connectFailure: Int = 0,
    val sendLimit: Int = 0,
    val transportError: Int = 0
){
    constructor(stats: SubProtocolWebSocketHandler.Stats): this(
        currentWebSockets = stats.webSocketSessions,
        totalSessions = stats.totalSessions,
        connectFailure = stats.noMessagesReceivedSessions,
        sendLimit = stats.limitExceededSessions,
        transportError = stats.transportErrorSessions,
    )
}


data class ThreadPoolMetric(
    val poolSize: Int = 0,
    val activeThreads: Int = 0,
    val queuedTasks: Int = 0,
    val completedTasks: Int = 0,
){
    constructor(executor: ThreadPoolExecutor): this(
        poolSize = executor.poolSize,
        activeThreads = executor.activeCount,
        queuedTasks = executor.queue.size,
        completedTasks = executor.completedTaskCount.toInt()
    )
}