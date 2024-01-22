package sj.messenger.domain.chat.controller

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import sj.messenger.domain.chat.dto.MessageDto
import sj.messenger.domain.chat.service.ChatService

@Controller
class ChatStompController(
    private val chatService: ChatService,
    private val messagingTemplate: SimpMessagingTemplate,
) {

    @MessageMapping("/topic/chat")
    fun sendMessage(messageDto : MessageDto){
        chatService.saveMessage(messageDto)
        messagingTemplate.convertAndSend("/chat/${messageDto.chatRoomId}",messageDto)
    }
}