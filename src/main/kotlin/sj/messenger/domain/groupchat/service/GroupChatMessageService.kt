package sj.messenger.domain.groupchat.service

import io.micrometer.core.annotation.Timed
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.BatchingRabbitTemplate
import org.springframework.data.domain.PageRequest
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import sj.messenger.domain.groupchat.domain.GroupMessage
import sj.messenger.domain.groupchat.dto.ServerGroupMessageDto
import sj.messenger.domain.groupchat.dto.ClientGroupMessageDto
import sj.messenger.domain.groupchat.repository.GroupMessageRepository
import java.time.LocalDateTime

@Service
class GroupChatMessageService (
    private val batchingRabbitTemplate: BatchingRabbitTemplate,
    private val groupMessageRepository: GroupMessageRepository
){
    @Async("threadPoolTaskExecutor")
    fun saveRequestAsync(groupMessageDto: ClientGroupMessageDto) {
        batchingRabbitTemplate.convertAndSend("groupMessageSaveQueue",groupMessageDto)
    }

    @Timed("service.group-chat-message.batch-save")
    @RabbitListener(queues = ["groupMessageSaveQueue"])
    fun saveAllReceivedMessage(messages : List<ClientGroupMessageDto>){
        val groupMessages = messages.map {
            GroupMessage(
                senderId = it.senderId,
                groupChatId = it.groupChatId,
                content = it.content,
                sentAt = it.sentAt
            )
        }.toList()
        groupMessageRepository.saveAll(groupMessages)
    }

    fun getPreviousMessages(groupChatId: Long, dateTime: LocalDateTime): List<ServerGroupMessageDto>{
        val pageable = PageRequest.of(0,10)
        val previousMessages = groupMessageRepository.findPreviousMessages(groupChatId,dateTime,pageable)
            .reversed()
        return previousMessages.map {
            ServerGroupMessageDto(
                groupChatId = it.groupChatId,
                senderId = it.senderId,
                content = it.content,
                receivedAt = it.sentAt
            )
        }
    }
}