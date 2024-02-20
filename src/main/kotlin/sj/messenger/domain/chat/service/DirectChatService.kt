package sj.messenger.domain.chat.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sj.messenger.domain.directchat.domain.DirectChat
import sj.messenger.domain.directchat.domain.DirectMessage
import sj.messenger.domain.directchat.dto.DirectChatDto
import sj.messenger.domain.directchat.dto.SentDirectMessageDto
import sj.messenger.domain.directchat.repository.DirectChatRepository
import sj.messenger.domain.directchat.repository.DirectMessageRepository
import sj.messenger.domain.user.dto.UserDto
import sj.messenger.domain.user.repository.UserRepository
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

    fun getUserDirectChats(userId: Long): List<DirectChatDto> {
        val directChats = directChatRepository.findAllByUserIdWithUsers(userId)
        return directChats.map {
            DirectChatDto(
                id = it.id!!,
                otherUser = UserDto(it.getOtherUser(myId = userId))
            )
        }
    }

    @Transactional
    fun createDirectChat(userIds: Pair<Long, Long>) : Long{
        validateAlreadyExists(userIds)
        val users = userService.findUsers(userIds.toList())
        val directChat = directChatRepository.save(DirectChat(users[0], users[1]))
        return directChat.id!!
    }

    private fun validateAlreadyExists(userIds: Pair<Long, Long>) {
        if (directChatRepository.existsByUserIds(userIds))
            throw RuntimeException("DirectChat already exists. users = (${userIds.first},${userIds.second})")
    }
}