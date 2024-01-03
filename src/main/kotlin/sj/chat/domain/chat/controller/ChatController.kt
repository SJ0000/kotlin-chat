package sj.chat.domain.chat.controller

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import sj.chat.domain.chat.dto.MessageDto
import sj.chat.domain.chat.service.ChatService

@Controller
class ChatController(
    private val chatService: ChatService,
    private val messagingTemplate: SimpMessagingTemplate,
) {

    @MessageMapping("/topic/chat/message")
    fun sendMessage(messageDto : MessageDto){
        chatService.saveMessage(messageDto)
        messagingTemplate.convertAndSend("/chatroom/${messageDto.chatRoomId}/message",)
    }
}