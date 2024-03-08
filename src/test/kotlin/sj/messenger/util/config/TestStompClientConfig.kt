package sj.messenger.util.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.stomp.*
import org.springframework.web.socket.WebSocketHttpHeaders
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import java.lang.reflect.Type
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

@TestConfiguration
class TestStompClientConfig(
    private val om: ObjectMapper,
) {
    @Value("\${server.port}")
    var port : Int? = null

    @Bean
    fun testStompClient() : TestStompClient{
        val connectionUrl = "ws://localhost:${port}/message-broker"
        return TestStompClient(connectionUrl, om)
    }
}


class TestStompClient(
    val connectionUrl: String,
    objectMapper: ObjectMapper,
) {
    val stompClient = WebSocketStompClient(StandardWebSocketClient())

    init {
        this.stompClient.messageConverter = MappingJackson2MessageConverter(objectMapper)
    }

    inline fun <reified T> sendAndReceive(source: String, destination: String, message: Any): T {
        val received = AtomicReference<T>()
        val failure = AtomicReference<Throwable>()
        val latch = CountDownLatch(1)

        val handler = TestSessionHandler(failure) { session, header ->
            session.subscribe(destination, object : StompFrameHandler {

                override fun getPayloadType(headers: StompHeaders): Type {
                    return T::class.java
                }

                override fun handleFrame(headers: StompHeaders, payload: Any?) {
                    val dto = payload as T
                    try {
                        println("payload = ${dto}")
                        received.set(dto)
                    } catch (t: Throwable) {
                        failure.set(t)
                    } finally {
                        session.disconnect()
                        latch.countDown()
                    }
                }
            })

            try {
                session.send(source, message)
                println("sent message = ${message}")
            } catch (t: Throwable) {
                failure.set(t)
                latch.countDown()
            }
        }

        stompClient.connectAsync(connectionUrl, WebSocketHttpHeaders(), handler)

        if (latch.await(3, TimeUnit.SECONDS)) {
            if (failure.get() != null)
                throw RuntimeException("", failure.get())
            return received.get()
        } else {
            throw RuntimeException("not received.")
        }
    }
}

class TestSessionHandler(
    private val failure: AtomicReference<Throwable>,
    private val onAfterConnected: (StompSession, StompHeaders) -> Unit
) : StompSessionHandlerAdapter() {

    override fun afterConnected(session: StompSession, connectedHeaders: StompHeaders) {
        onAfterConnected(session, connectedHeaders)
    }

    override fun handleFrame(headers: StompHeaders, payload: Any?) {
        this.failure.set(Exception(headers.toString()))
    }

    override fun handleException(
        session: StompSession,
        command: StompCommand?,
        headers: StompHeaders,
        payload: ByteArray,
        exception: Throwable
    ) {
        this.failure.set(exception)
    }

    override fun handleTransportError(session: StompSession, exception: Throwable) {
        this.failure.set(exception)
    }
}