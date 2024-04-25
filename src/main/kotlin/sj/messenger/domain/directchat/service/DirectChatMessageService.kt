package sj.messenger.domain.directchat.service

import io.micrometer.core.annotation.Timed
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.BatchingRabbitTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sj.messenger.domain.directchat.domain.DirectChat
import sj.messenger.domain.directchat.domain.DirectMessage
import sj.messenger.domain.directchat.dto.SentDirectMessageDto
import sj.messenger.domain.directchat.repository.DirectChatRepository
import sj.messenger.domain.directchat.repository.DirectMessageRepository
import sj.messenger.domain.groupchat.domain.GroupMessage
import sj.messenger.domain.groupchat.dto.SentGroupMessageDto
import sj.messenger.domain.user.service.UserService

@Service
class DirectChatMessageService(
    private val batchingRabbitTemplate: BatchingRabbitTemplate,
    private val directMessageRepository: DirectMessageRepository,
) {
    fun saveMessage(sentDirectMessageDto: SentDirectMessageDto): String {
        val message = DirectMessage(
            senderId = sentDirectMessageDto.senderId,
            directChatId = sentDirectMessageDto.directChatId,
            content = sentDirectMessageDto.content,
            sentAt = sentDirectMessageDto.sentAt,
        )
        val savedMessage = directMessageRepository.save(message)
        return savedMessage.id!!.toHexString()
    }

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
}