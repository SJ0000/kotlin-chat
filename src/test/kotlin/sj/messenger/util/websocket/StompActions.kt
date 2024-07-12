package sj.messenger.util.websocket

import org.springframework.messaging.simp.stomp.StompFrameHandler
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import java.lang.reflect.Type

class StompAction(
    private val action: (StompSession, StompHeaders) -> Unit
) : (StompSession, StompHeaders) -> Unit {
    override fun invoke(p1: StompSession, p2: StompHeaders) {
        action(p1, p2);
    }
}

inline fun <reified T> getSubscribeAction(
    destination: String,
    holder: TestAsyncResultHolder<T>
): StompAction {
    return StompAction{ session: StompSession, header: StompHeaders ->
        session.subscribe(destination, object : StompFrameHandler {
            override fun getPayloadType(headers: StompHeaders): Type {
                return T::class.java
            }

            override fun handleFrame(headers: StompHeaders, payload: Any?) {
                val dto = payload as T
                try {
                    holder.received.set(dto)
                } catch (t: Throwable) {
                    holder.failure.set(t)
                } finally {
                    session.disconnect()
                    holder.latch.countDown()
                }
            }
        })
    }
}

inline fun <reified T> getSendAction(source: String, message: Any, holder: TestAsyncResultHolder<T>): StompAction {
    return StompAction(){ session: StompSession, header: StompHeaders ->
        try {
            session.send(source, message)
        } catch (t: Throwable) {
            holder.failure.set(t)
            holder.latch.countDown()
        }
    }
}