package sj.messenger.domain.chat.controller

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import sj.messenger.domain.chat.dto.ReceivedMessageDto
import sj.messenger.domain.chat.dto.SentMessageDto
import sj.messenger.domain.chat.service.ChatService
import java.time.LocalDateTime

@Controller
class ChatStompController(
    private val chatService: ChatService,
) {

    @MessageMapping("/chat-message")
    @SendTo("/topic/chat")
    fun sendMessage(sentMessageDto: SentMessageDto) : ReceivedMessageDto {
        val messageId = chatService.saveMessage(sentMessageDto)
        val data = ReceivedMessageDto(
            id = messageId,
            chatRoomId = sentMessageDto.chatRoomId,
            senderId = sentMessageDto.senderId,
            content = sentMessageDto.content,
            receivedAt = LocalDateTime.now()
        )
        println(data)
        return data
    }
}