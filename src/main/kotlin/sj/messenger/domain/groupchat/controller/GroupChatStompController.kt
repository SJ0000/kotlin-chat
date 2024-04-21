package sj.messenger.domain.groupchat.controller

import io.micrometer.core.annotation.Timed
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import sj.messenger.domain.groupchat.dto.ReceivedGroupMessageDto
import sj.messenger.domain.groupchat.dto.SentGroupMessageDto
import sj.messenger.domain.groupchat.service.GroupChatMessageService
import java.time.LocalDateTime

@Controller
class GroupChatStompController(
    private val template : SimpMessagingTemplate,
    private val groupChatMessageService: GroupChatMessageService
) {

    @Timed("controller.group-chat-stomp.send-message")
    @MessageMapping("/group-message")
    fun sendMessage(
        @Payload sentGroupMessageDto: SentGroupMessageDto,
    ) {
        val messageId = groupChatMessageService.saveMessage(sentGroupMessageDto)
        val data = ReceivedGroupMessageDto(
            id = messageId,
            groupChatId = sentGroupMessageDto.groupChatId,
            senderId = sentGroupMessageDto.senderId,
            content = sentGroupMessageDto.content,
            receivedAt = LocalDateTime.now()
        )
        template.convertAndSend("/topic/group-chat/${data.groupChatId}",data)
    }

    @Timed("controller.group-chat-stomp.send-message-bypass")
    @MessageMapping("/group-message/bypass")
    fun sendMessageBypass(
        @Payload sentGroupMessageDto: SentGroupMessageDto,
    ) {
        val data = ReceivedGroupMessageDto(
            id = "not saved",
            groupChatId = sentGroupMessageDto.groupChatId,
            senderId = sentGroupMessageDto.senderId,
            content = sentGroupMessageDto.content,
            receivedAt = LocalDateTime.now()
        )
        template.convertAndSend("/topic/group-chat/bypass/${data.groupChatId}",data)
    }
}