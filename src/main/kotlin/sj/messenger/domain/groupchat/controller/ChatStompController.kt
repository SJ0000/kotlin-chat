package sj.messenger.domain.groupchat.controller

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import sj.messenger.domain.groupchat.dto.ReceivedMessageDto
import sj.messenger.domain.groupchat.dto.SentMessageDto
import sj.messenger.domain.groupchat.service.ChatService
import java.time.LocalDateTime

@Controller
class ChatStompController(
    private val template : SimpMessagingTemplate,
    private val chatService: ChatService,
) {

    @MessageMapping("/chat-message")
    fun sendMessage(
        @Payload sentMessageDto: SentMessageDto,
    ) {
        val messageId = chatService.saveMessage(sentMessageDto)
        val data = ReceivedMessageDto(
            id = messageId,
            chatRoomId = sentMessageDto.chatRoomId,
            senderId = sentMessageDto.senderId,
            content = sentMessageDto.content,
            receivedAt = LocalDateTime.now()
        )
        template.convertAndSend("/topic/chat/${data.chatRoomId}",data)
    }
}