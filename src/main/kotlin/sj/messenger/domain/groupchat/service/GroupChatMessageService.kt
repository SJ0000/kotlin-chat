package sj.messenger.domain.groupchat.service

import io.micrometer.core.annotation.Timed
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.BatchingRabbitTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.util.StopWatch
import sj.messenger.domain.groupchat.domain.GroupMessage
import sj.messenger.domain.groupchat.dto.SentGroupMessageDto
import sj.messenger.domain.groupchat.repository.GroupMessageRepository

@Service
class GroupChatMessageService (
    private val batchingRabbitTemplate: BatchingRabbitTemplate,
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

    /*
        TODO
            GroupChat, DirectChat 다
            1. 로컬 환경에서 bulk insert 테스트 (K6로 메시지 대량 보내서)
            2. 테스트코드 작성 (통합테스트)
            3. oci-infra에 docker rabbitmq 추가
            4. git push 전 github secret에 테스트 config, 배포 config 추가
            5. 프론트엔드 수정 (message id를 받을 수 없음)
     */

    @Async("threadPoolTaskExecutor")
    fun saveRequestAsync(groupMessage: SentGroupMessageDto) {
        println("groupMessage = ${groupMessage}")
        batchingRabbitTemplate.convertAndSend("groupMessageSaveQueue",groupMessage)
    }

    @Timed("service.group-chat-message.batch-save")
    @RabbitListener(queues = ["groupMessageSaveQueue"])
    fun saveAllReceivedMessage(messages : List<SentGroupMessageDto>){
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
}