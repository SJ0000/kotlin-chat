package sj.messenger.domain.chat.controller

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import sj.messenger.domain.chat.dto.ReceivedDirectMessageDto
import sj.messenger.domain.chat.dto.ReceivedMessageDto
import sj.messenger.domain.chat.dto.SentDirectMessageDto
import sj.messenger.domain.chat.dto.SentMessageDto
import sj.messenger.domain.chat.service.ChatService
import sj.messenger.domain.chat.service.DirectChatService
import sj.messenger.domain.security.authentication.principal.LoginUserDetails
import java.security.Principal
import java.time.LocalDateTime

@Controller
class ChatStompController(
    private val template : SimpMessagingTemplate,
    private val chatService: ChatService,
    private val directChatService: DirectChatService,
) {

    @MessageMapping("/chat-message")
//    @SendTo("/topic/chat")
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

    @MessageMapping("/direct-message")
    @SendTo("/topic/direct-chat")
    fun sendDirectMessage(
        @Payload messageDto: SentDirectMessageDto,
    ) : ReceivedDirectMessageDto {
        val messageId = directChatService.saveMessage(messageDto)
        val data = ReceivedDirectMessageDto(
            id = messageId,
            directChatId = messageDto.directChatId,
            senderId = messageDto.senderId,
            content = messageDto.content,
            receivedAt = LocalDateTime.now()
        )
        return data
    }

}