package sj.messenger.domain.chat.service

import org.springframework.stereotype.Service
import sj.messenger.domain.directchat.domain.DirectMessage
import sj.messenger.domain.directchat.dto.DirectChatDto
import sj.messenger.domain.chat.dto.SentDirectMessageDto
import sj.messenger.domain.directchat.repository.DirectChatRepository
import sj.messenger.domain.directchat.repository.DirectMessageRepository
import sj.messenger.domain.user.dto.UserDto
import sj.messenger.domain.user.service.UserService

@Service
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

    fun validateExists(userId1 : Long, userId2: Long){
    }
}