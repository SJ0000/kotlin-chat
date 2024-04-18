package sj.messenger.global.actuator.websocket


interface StompStatsProvider {
    fun getWebSocketSessionStats(): WebSocketSessionStats;
}