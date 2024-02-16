package sj.messenger.domain.chat.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sj.messenger.domain.chat.domain.ChatRoom
import sj.messenger.domain.chat.domain.Message
import sj.messenger.domain.chat.dto.ChatRoomCreate
import sj.messenger.domain.chat.dto.SentMessageDto
import sj.messenger.domain.chat.repository.ChatRoomRepository
import sj.messenger.domain.chat.repository.MessageRepository
import sj.messenger.domain.chat.repository.ParticipantRepository
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
    fun saveMessage(sentMessageDto: SentMessageDto) : String {
        val message = Message(
            senderId = sentMessageDto.senderId,
            chatRoomId = sentMessageDto.chatRoomId,
            content = sentMessageDto.content,
            sentAt = sentMessageDto.sentAt
        )
        val savedMessage = messageRepository.save(message)
        return savedMessage.id!!.toHexString()
    }

    fun getChatRoom(id: Long): ChatRoom {
        return chatRoomRepository.findByIdOrNull(id) ?: throw RuntimeException("chat room id '$id' not found")
    }

    @Transactional(readOnly = false)
    fun joinChatRoom(chatRoomId: Long, userId: Long) {
        val chatRoom = chatRoomRepository.findByIdOrNull(chatRoomId) ?: throw RuntimeException("chat room id '$chatRoomId' not found")
        val user = userService.findUserById(userId)
        chatRoom.join(user)
    }

    @Transactional(readOnly = false)
    fun createChatRoom(chatRoomCreate: ChatRoomCreate): Long {
        val chatRoom = ChatRoom(name = chatRoomCreate.name)
        chatRoomRepository.save(chatRoom)
        return chatRoom.id ?: throw RuntimeException("created chatroom id is null")
    }

    fun findUserChatRooms(userId: Long): List<ChatRoom>{
        val participants = participantRepository.getParticipantsByUserId(userId)
        return participants.map { it.chatRoom }
    }

    fun findChatRoom(chatRoomId: Long): ChatRoom{
        return chatRoomRepository.findByIdOrNull(chatRoomId) ?: throw RuntimeException("ChatRoom id ${chatRoomId} not found")
    }

    fun findChatRoomWithParticipants(chatRoomId: Long): ChatRoom{
        return chatRoomRepository.findWithParticipantsById(chatRoomId) ?: throw RuntimeException("ChatRoom id ${chatRoomId} not found")
    }
}