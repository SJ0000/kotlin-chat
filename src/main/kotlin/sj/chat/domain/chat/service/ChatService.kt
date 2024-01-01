package sj.chat.domain.chat.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import sj.chat.domain.chat.domain.ChatRoom
import sj.chat.domain.chat.repository.ChatRoomRepository


@Service
class ChatService(
    private val chatRoomRepository: ChatRoomRepository,
) {

    fun getChatRoom(id : Long) : ChatRoom{
        return chatRoomRepository.findByIdOrNull(id) ?: throw RuntimeException("chat room id '$id' not found")
    }


    fun joinChatRoom(chatRoomId : Long, userId : Long){
        // STOMP Subscribe

    }

    fun createChatRoom(name: String, maxCapacity: Int) : Long {
        val chatRoom = ChatRoom(name = name, maxCapacity = maxCapacity)
        chatRoomRepository.save(chatRoom)
        return chatRoom.id!!
    }

}