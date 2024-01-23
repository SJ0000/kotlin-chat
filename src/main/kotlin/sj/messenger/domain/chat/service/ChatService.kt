package sj.messenger.domain.chat.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sj.messenger.domain.chat.domain.ChatRoom
import sj.messenger.domain.chat.domain.Message
import sj.messenger.domain.chat.dto.MessageDto
import sj.messenger.domain.chat.repository.ChatRoomRepository
import sj.messenger.domain.chat.repository.MessageRepository
import sj.messenger.domain.chat.repository.ParticipantRepository
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.domain.user.service.UserService


@Service
@Transactional(readOnly = true)
class ChatService(
    private val chatRoomRepository: ChatRoomRepository,
    private val participantRepository: ParticipantRepository,
    private val messageRepository: MessageRepository,

    private val userService: UserService,
) {
    @Transactional(readOnly = false)
    fun saveMessage(messageDto: MessageDto) {
        val message = Message(
            senderId = messageDto.senderId,
            chatRoomId = messageDto.chatRoomId,
            content = messageDto.content,
            sentAt = messageDto.sentAt
        )
        messageRepository.save(message)
    }

    fun getChatRoom(id: Long): ChatRoom {
        return chatRoomRepository.findByIdOrNull(id) ?: throw RuntimeException("chat room id '$id' not found")
    }

    @Transactional(readOnly = false)
    fun joinChatRoom(chatRoomId: Long, userId: Long) {
        val chatRoom = chatRoomRepository.findByIdOrNull(chatRoomId) ?: throw RuntimeException("chat room id '$chatRoomId' not found")
        val user = userService.findUser(userId)
        chatRoom.join(user)
    }

    @Transactional(readOnly = false)
    fun createChatRoom(): Long {
        val chatRoom = ChatRoom()
        chatRoomRepository.save(chatRoom)
        return chatRoom.id ?: throw RuntimeException("created chatroom id is null")
    }

    fun findUserChatRooms(userId: Long): List<ChatRoom>{
        val participants = participantRepository.getParticipantsByUserId(userId)
        return participants.map { it.chatRoom }
    }
}