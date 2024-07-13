package sj.messenger.util.websocket

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicReference

@TestConfiguration
class TestStompClientConfig(
    private val om: ObjectMapper,
) {
    @Value("\${server.port}")
    var port: Int? = null

    @Bean
    fun testStompClient(): TestStompClient {
        val connectionUrl = "ws://localhost:${port}/message-broker"
        return TestStompClient(connectionUrl, om)
    }
}



class TestAsyncResultHolder<T> {
    val received = AtomicReference<T>()
    val failure = AtomicReference<Throwable>()
    val latch = CountDownLatch(1)
}