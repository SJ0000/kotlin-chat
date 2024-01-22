package sj.messenger.domain.chat.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import sj.messenger.domain.chat.domain.ChatRoom
import sj.messenger.domain.chat.domain.Message
import sj.messenger.domain.chat.dto.MessageDto
import sj.messenger.domain.chat.repository.ChatRoomRepository
import sj.messenger.domain.chat.repository.MessageRepository
import sj.messenger.domain.chat.repository.ParticipantRepository


@Service
class ChatService(
    private val chatRoomRepository: ChatRoomRepository,
    private val participantRepository: ParticipantRepository,
    private val messageRepository: MessageRepository,
) {

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

    fun joinChatRoom(chatRoomId: Long, userId: Long) {

    }

    fun createChatRoom(): Long {
        val chatRoom = ChatRoom()
        chatRoomRepository.save(chatRoom)
        return chatRoom.id ?: throw RuntimeException("created chatroom id is null")
    }

}