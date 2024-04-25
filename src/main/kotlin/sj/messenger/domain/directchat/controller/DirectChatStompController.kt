package sj.messenger.domain.directchat.controller

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import sj.messenger.domain.directchat.dto.DirectMessageType.MESSAGE
import sj.messenger.domain.directchat.dto.ReceivedDirectMessageDto
import sj.messenger.domain.directchat.dto.SentDirectMessageDto
import sj.messenger.domain.directchat.service.DirectChatMessageService
import java.time.LocalDateTime

@Controller
class DirectChatStompController(
    private val template : SimpMessagingTemplate,
    private val directChatMessageService: DirectChatMessageService,
) {
    @MessageMapping("/direct-message")
    fun directMessage(
        @Payload messageDto: SentDirectMessageDto,
    ) {
        when (messageDto.messageType) {
            MESSAGE -> processMessage(messageDto)
            else -> throw RuntimeException("Not Supported Message Type.")
        }
    }

    private fun processMessage(message: SentDirectMessageDto){
        directChatMessageService.saveRequestAsync(message)
        val data = ReceivedDirectMessageDto(
            directChatId = message.directChatId,
            senderId = message.senderId,
            content = message.content,
            messageType = message.messageType,
            receivedAt = LocalDateTime.now()
        )
        template.convertAndSend("/topic/direct-chat/${message.senderId}", data)
        template.convertAndSend("/topic/direct-chat/${message.receiverId}", data)
    }
}