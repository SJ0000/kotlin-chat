package sj.messenger.domain.directchat.controller

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import sj.messenger.domain.chat.service.DirectChatService
import sj.messenger.domain.directchat.dto.DirectMessageType
import sj.messenger.domain.directchat.dto.DirectMessageType.*
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
        when(messageDto.messageType){
            MESSAGE -> processMessage(messageDto)
            CHAT_START -> processChatStart(messageDto)
            else -> throw RuntimeException("Not Supported Message Type.")
        }
    }

    /*
        processMessage, procesChatStart가 현재는 같은 로직이지만
        추후 변경에 대비해 메서드를 분리
    */

    private fun processMessage(message: SentDirectMessageDto){
        val messageId = directChatService.saveMessage(message)
        val data = ReceivedDirectMessageDto(
            id = messageId,
            directChatId = message.directChatId,
            senderId = message.senderId,
            content = message.content,
            receivedAt = LocalDateTime.now()
        )
        template.convertAndSend("/topic/direct-chat/${message.receiverId}", data)
    }

    private fun processChatStart(message: SentDirectMessageDto){
        val messageId = directChatService.saveMessage(message)
        val data = ReceivedDirectMessageDto(
            id = messageId,
            directChatId = message.directChatId,
            senderId = message.senderId,
            content = message.content,
            receivedAt = LocalDateTime.now()
        )
        template.convertAndSend("/topic/direct-chat/${message.receiverId}", data)
    }
}