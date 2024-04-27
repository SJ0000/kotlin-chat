package sj.messenger.domain.directchat.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sj.messenger.domain.directchat.domain.DirectChat
import sj.messenger.domain.directchat.domain.DirectMessage
import sj.messenger.domain.directchat.dto.SentDirectMessageDto
import sj.messenger.domain.directchat.repository.DirectChatRepository
import sj.messenger.domain.directchat.repository.DirectMessageRepository
import sj.messenger.domain.user.service.UserService

@Service
@Transactional(readOnly = true)
class DirectChatService(
    private val userService: UserService,
    private val directChatRepository: DirectChatRepository,
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

    fun getDirectChat(userId: Long, id: Long): DirectChat {
        val directChat =
            directChatRepository.findByIdWithUsers(id) ?: throw RuntimeException("DirectChat not exists. id = ${id}")
        if(!directChat.hasAuthority(userId))
            throw RuntimeException("User(id=${userId}) has no permission. DirectChat(id = ${id})")
        return directChat
    }

    fun getUserDirectChats(userId: Long): List<DirectChat> {
        return directChatRepository.findAllByUserIdWithUsers(userId)
    }

    @Transactional
    fun createDirectChat(userIds: Pair<Long, Long>): Long {
        validateAlreadyExists(userIds)
        val users = userService.findUsers(setOf(userIds.first, userIds.second))
        val directChat = directChatRepository.save(DirectChat(users[0], users[1]))
        return directChat.id!!
    }

    private fun validateAlreadyExists(userIds: Pair<Long, Long>) {
        if (directChatRepository.existsByUserIds(userIds))
            throw RuntimeException("DirectChat already exists. users = (${userIds.first},${userIds.second})")
    }
}