package sj.messenger.domain.directchat.controller

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import sj.messenger.domain.chat.service.DirectChatService
import sj.messenger.domain.directchat.dto.ReceivedDirectMessageDto
import sj.messenger.domain.directchat.dto.SentDirectMessageDto
import java.time.LocalDateTime

@Controller
class DirectChatStompController(
    private val template : SimpMessagingTemplate,
    private val directChatService: DirectChatService,
) {
    @MessageMapping("/direct-message")
    fun sendDirectMessage(
        @Payload messageDto: SentDirectMessageDto,
    ) {
        val messageId = directChatService.saveMessage(messageDto)
        val data = ReceivedDirectMessageDto(
            id = messageId,
            directChatId = messageDto.directChatId,
            senderId = messageDto.senderId,
            content = messageDto.content,
            receivedAt = LocalDateTime.now()
        )
        template.convertAndSend("/topic/direct-chat/${messageDto.receiverId}", data)
    }
}