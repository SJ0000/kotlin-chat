package sj.messenger.util.websocket

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.stomp.*
import org.springframework.web.socket.WebSocketHttpHeaders
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import java.util.concurrent.TimeUnit

class TestStompClient(
    val connectionUrl: String,
    objectMapper: ObjectMapper,
) {
    val stompClient = WebSocketStompClient(StandardWebSocketClient())

    init {
        this.stompClient.messageConverter = MappingJackson2MessageConverter(objectMapper)
    }

    inline fun <reified T> sendAndReceive(source: String, destination: String, message: Any): T {
        val holder = TestAsyncResultHolder<T>()

        val subscribeAction = getSubscribeAction(destination, holder);
        val sendAction = getSendAction(source,message,holder);

        val handler = TestSessionHandler(holder, subscribeAction, sendAction)

        stompClient.connectAsync(connectionUrl, WebSocketHttpHeaders(), handler)

        if (holder.latch.await(3, TimeUnit.SECONDS)) {
            if (holder.failure.get() != null)
                throw RuntimeException("", holder.failure.get())
            return holder.received.get()
        } else {
            throw RuntimeException("not received.")
        }
    }
}

class TestSessionHandler<T>(
    private val holder: TestAsyncResultHolder<T>,
    private vararg val afterConnectedActions: StompAction
) : StompSessionHandlerAdapter() {

    override fun afterConnected(session: StompSession, connectedHeaders: StompHeaders) {
        afterConnectedActions.forEach {
            it(session, connectedHeaders)
        }
    }

    override fun handleFrame(headers: StompHeaders, payload: Any?) {
        holder.failure.set(Exception(headers.toString()))
    }

    override fun handleException(
        session: StompSession,
        command: StompCommand?,
        headers: StompHeaders,
        payload: ByteArray,
        exception: Throwable
    ) {
        holder.failure.set(exception)
    }

    override fun handleTransportError(session: StompSession, exception: Throwable) {
        holder.failure.set(exception)
    }
}