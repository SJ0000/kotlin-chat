package sj.messenger.domain.groupchat.controller

import io.micrometer.core.annotation.Timed
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import sj.messenger.domain.groupchat.dto.ServerGroupMessageDto
import sj.messenger.domain.groupchat.dto.ClientGroupMessageDto
import sj.messenger.domain.groupchat.service.GroupChatMessageService
import sj.messenger.global.stomp.CONTENT_CLASS_NAME
import java.time.LocalDateTime

@Controller
class GroupChatStompController(
    private val template : SimpMessagingTemplate,
    private val groupChatMessageService: GroupChatMessageService
) {

    @Timed("controller.group-chat-stomp.send-message")
    @MessageMapping("/group-message")
    fun sendMessage(
        @Payload clientGroupMessageDto: ClientGroupMessageDto,
    ) {
        groupChatMessageService.saveRequestAsync(clientGroupMessageDto)
        val data = ServerGroupMessageDto(
            groupChatId = clientGroupMessageDto.groupChatId,
            senderId = clientGroupMessageDto.senderId,
            content = clientGroupMessageDto.content,
            receivedAt = LocalDateTime.now()
        )
        val header = mapOf(Pair(CONTENT_CLASS_NAME, data.javaClass.simpleName))
        template.convertAndSend("/topic/group-chat/${data.groupChatId}",data, header)
    }
}