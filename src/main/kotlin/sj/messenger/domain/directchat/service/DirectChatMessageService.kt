package sj.messenger.domain.directchat.service

import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.BatchingRabbitTemplate
import org.springframework.data.domain.PageRequest
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import sj.messenger.domain.directchat.domain.DirectMessage
import sj.messenger.domain.directchat.dto.DirectMessageType
import sj.messenger.domain.directchat.dto.ServerDirectMessageDto
import sj.messenger.domain.directchat.dto.ClientDirectMessageDto
import sj.messenger.domain.directchat.repository.DirectMessageRepository
import java.time.LocalDateTime

@Service
class DirectChatMessageService(
    private val batchingRabbitTemplate: BatchingRabbitTemplate,
    private val directMessageRepository: DirectMessageRepository,
) {

    @Async("threadPoolTaskExecutor")
    fun saveRequestAsync(directMessageDto: ClientDirectMessageDto) {
        val message = DirectMessage(
            senderId = directMessageDto.senderId,
            directChatId = directMessageDto.directChatId,
            content = directMessageDto.content,
            sentAt = LocalDateTime.now(),
        )
        batchingRabbitTemplate.convertAndSend("directMessageSaveQueue",message)
    }

    @RabbitListener(queues = ["directMessageSaveQueue"])
    fun saveAllReceivedMessage(messages : List<DirectMessage>){
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

    fun getPreviousMessages(directChatId: Long, dateTime: LocalDateTime): List<ServerDirectMessageDto>{
        val pageable = PageRequest.of(0, 10)
        val previousMessages = directMessageRepository.findPreviousMessages(directChatId, dateTime, pageable)
            .reversed()
        return previousMessages.map {
            ServerDirectMessageDto(
                directChatId = it.directChatId,
                messageType = DirectMessageType.MESSAGE,
                senderId = it.senderId,
                content = it.content,
                receivedAt = it.sentAt
            )
        }
    }
}