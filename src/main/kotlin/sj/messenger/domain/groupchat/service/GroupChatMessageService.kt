package sj.messenger.domain.groupchat.service

import io.micrometer.core.annotation.Timed
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import sj.messenger.domain.groupchat.domain.GroupMessage
import sj.messenger.domain.groupchat.dto.SentGroupMessageDto
import sj.messenger.domain.groupchat.repository.GroupMessageRepository

@Service
class GroupChatMessageService (
    private val groupMessageRepository: GroupMessageRepository
){
    @Timed("service.group-chat-message.save-message")
    fun saveMessage(sentGroupMessageDto: SentGroupMessageDto) : String {
        val groupMessage = GroupMessage(
            senderId = sentGroupMessageDto.senderId,
            groupChatId = sentGroupMessageDto.groupChatId,
            content = sentGroupMessageDto.content,
            sentAt = sentGroupMessageDto.sentAt
        )
        val savedMessage = groupMessageRepository.save(groupMessage)
        return savedMessage.id!!.toHexString()
    }

    @Async
    @Timed("service.group-chat-message.save-message-async")
    fun saveMessageAsync(sentGroupMessageDto: SentGroupMessageDto) {
        val groupMessage = GroupMessage(
            senderId = sentGroupMessageDto.senderId,
            groupChatId = sentGroupMessageDto.groupChatId,
            content = sentGroupMessageDto.content,
            sentAt = sentGroupMessageDto.sentAt
        )
        groupMessageRepository.save(groupMessage)
    }
}