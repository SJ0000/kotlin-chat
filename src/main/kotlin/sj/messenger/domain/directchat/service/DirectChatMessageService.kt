package sj.messenger.domain.directchat.service

import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.BatchingRabbitTemplate
import org.springframework.data.domain.PageRequest
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import sj.messenger.domain.directchat.domain.DirectMessage
import sj.messenger.domain.directchat.dto.DirectMessageType
import sj.messenger.domain.directchat.dto.ReceivedDirectMessageDto
import sj.messenger.domain.directchat.dto.SentDirectMessageDto
import sj.messenger.domain.directchat.repository.DirectMessageRepository
import java.time.LocalDateTime

@Service
class DirectChatMessageService(
    private val batchingRabbitTemplate: BatchingRabbitTemplate,
    private val directMessageRepository: DirectMessageRepository,
) {

    @Async("threadPoolTaskExecutor")
    fun saveRequestAsync(directMessageDto: SentDirectMessageDto) {
        batchingRabbitTemplate.convertAndSend("directMessageSaveQueue",directMessageDto)
    }

    @RabbitListener(queues = ["directMessageSaveQueue"])
    fun saveAllReceivedMessage(messages : List<SentDirectMessageDto>){
        val directMessages = messages.map {
            DirectMessage(
                senderId = it.senderId,
                directChatId = it.directChatId,
                content = it.content,
                sentAt = it.sentAt,
            )
        }.toList()
        directMessageRepository.saveAll(directMessages)
    }

    fun getPreviousMessages(directChatId: Long, dateTime: LocalDateTime): List<ReceivedDirectMessageDto>{
        val pageable = PageRequest.of(0, 10)
        val previousMessages = directMessageRepository.findPreviousMessages(directChatId, dateTime, pageable)
            .reversed()
        return previousMessages.map {
            ReceivedDirectMessageDto(
                directChatId = it.directChatId,
                messageType = DirectMessageType.MESSAGE,
                senderId = it.senderId,
                content = it.content,
                receivedAt = it.sentAt
            )
        }
    }
}