package sj.messenger.domain.groupchat.controller

import io.micrometer.core.annotation.Timed
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import sj.messenger.domain.groupchat.dto.ReceivedGroupMessageDto
import sj.messenger.domain.groupchat.dto.SentGroupMessageDto
import sj.messenger.domain.groupchat.service.GroupChatService
import java.time.LocalDateTime

@Controller
class GroupChatStompController(
    private val template : SimpMessagingTemplate,
    private val groupChatService: GroupChatService,
) {

    @Timed("controller.group-chat-stomp.send-message")
    @MessageMapping("/group-message")
    fun sendMessage(
        @Payload sentGroupMessageDto: SentGroupMessageDto,
    ) {
        val messageId = groupChatService.saveMessage(sentGroupMessageDto)
        val data = ReceivedGroupMessageDto(
            id = messageId,
            groupChatId = sentGroupMessageDto.groupChatId,
            senderId = sentGroupMessageDto.senderId,
            content = sentGroupMessageDto.content,
            receivedAt = LocalDateTime.now()
        )
        template.convertAndSend("/topic/group-chat/${data.groupChatId}",data)
    }
}