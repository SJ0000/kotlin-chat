package sj.chat.domain.chat.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import sj.chat.domain.chat.domain.ChatRoom
import sj.chat.domain.chat.domain.Message
import sj.chat.domain.chat.dto.MessageDto
import sj.chat.domain.chat.repository.ChatRoomRepository
import sj.chat.domain.chat.repository.MessageRepository
import java.time.LocalDateTime


@Service
class ChatService(
    private val chatRoomRepository: ChatRoomRepository,
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

    fun createChatRoom(name: String, maxCapacity: Int): Long {
        val chatRoom = ChatRoom(name = name, maxCapacity = maxCapacity)
        chatRoomRepository.save(chatRoom)
        return chatRoom.id ?: throw RuntimeException("created chatroom id is null")
    }

}