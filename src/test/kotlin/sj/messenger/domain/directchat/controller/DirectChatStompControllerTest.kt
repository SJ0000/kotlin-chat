package sj.messenger.domain.directchat.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.stomp.*
import org.springframework.web.socket.WebSocketHttpHeaders
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import sj.messenger.domain.directchat.dto.DirectMessageType
import sj.messenger.domain.directchat.dto.ReceivedDirectMessageDto
import sj.messenger.domain.directchat.dto.SentDirectMessageDto
import sj.messenger.util.integration.EnableContainers
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableContainers
class DirectChatStompControllerTest(
    @Autowired val om : ObjectMapper
) {
    @LocalServerPort
    var port = 0

    lateinit var stompClient: WebSocketStompClient
    val headers = WebSocketHttpHeaders()

    @BeforeEach
    fun setup() {
        stompClient = WebSocketStompClient(StandardWebSocketClient())
        stompClient.messageConverter = MappingJackson2MessageConverter(om)
    }

    @Test
    fun directMessage() {
        val latch = CountDownLatch(1)
        val failure = AtomicReference<Throwable>()


        val handler = object : TestSessionHandler(failure) {

            override fun afterConnected(session: StompSession, connectedHeaders: StompHeaders) {
                session.subscribe("/topic/direct-chat/1", object : StompFrameHandler {
                    override fun getPayloadType(headers: StompHeaders): Type {
                        return ReceivedDirectMessageDto::class.java
                    }

                    override fun handleFrame(headers: StompHeaders, payload: Any?) {
                        val dto = payload as ReceivedDirectMessageDto
                        try {
                            assertThat(dto.directChatId).isEqualTo(1L)
                            assertThat(dto.messageType).isEqualTo(DirectMessageType.MESSAGE)
                            assertThat(dto.senderId).isEqualTo(1L)
                            assertThat(dto.content).isEqualTo("1234")
                        } catch (t: Throwable) {
                            failure.set(t)
                        } finally {
                            session.disconnect()
                            latch.countDown()
                        }
                    }
                })

                try {
                    val message = SentDirectMessageDto(
                        directChatId = 1L,
                        messageType = DirectMessageType.MESSAGE,
                        senderId = 1L,
                        receiverId = 1L,
                        content = "1234",
                        sentAt = LocalDateTime.now()
                    )
                    session.send(
                        "/app/direct-message",
                        message
                    )
                    println("sent message = ${message}")
                } catch (t: Throwable) {
                    failure.set(t)
                    latch.countDown()
                }
            }
        }
        stompClient.connectAsync("ws://localhost:{port}/message-broker", this.headers, handler, this.port)

        if (latch.await(3, TimeUnit.SECONDS)) {
            if (failure.get() != null)
                throw AssertionError("", failure.get())
        } else {
            fail("Greeting not received")
        }
    }
}


open class TestSessionHandler(
    private val failure: AtomicReference<Throwable>,
) : StompSessionHandlerAdapter() {

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