package sj.messenger.domain.notification.interceptor

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.NativeMessageHeaderAccessor
import sj.messenger.domain.directchat.dto.ServerDirectMessageDto
import sj.messenger.domain.groupchat.dto.ServerGroupMessageDto
import sj.messenger.domain.notification.service.NotificationService
import sj.messenger.global.stomp.CONTENT_CLASS_NAME


class NotificationInterceptor(
    private val notificationService: NotificationService,
    private val objectMapper: ObjectMapper
) : ChannelInterceptor {

    private fun notifyDirectMessage(message: ServerDirectMessageDto) {
        notificationService.sendDirectNotification(message.senderId, message.directChatId, message.content)
    }

    private fun notifyGroupMessage(message: ServerGroupMessageDto) {
        notificationService.sendGroupNotification(message.senderId, message.groupChatId, message.content)
    }

    override fun postSend(message: Message<*>, channel: MessageChannel, sent: Boolean) {
        val contentClassName = NativeMessageHeaderAccessor.getFirstNativeHeader(CONTENT_CLASS_NAME, message.headers)
        when (contentClassName) {
            ServerDirectMessageDto::class.simpleName -> notifyDirectMessage(deserialize<ServerDirectMessageDto>(message.payload as ByteArray))
            ServerGroupMessageDto::class.simpleName -> notifyGroupMessage(deserialize<ServerGroupMessageDto>(message.payload as ByteArray))
        }

    }

    private inline fun <reified T> deserialize(byteArray: ByteArray): T {
        return objectMapper.readValue(byteArray, T::class.java);
    }
}