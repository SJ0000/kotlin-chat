package sj.messenger.domain.groupchat.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sj.messenger.domain.groupchat.domain.GroupChat
import sj.messenger.domain.groupchat.domain.Message
import sj.messenger.domain.groupchat.dto.GroupChatCreate
import sj.messenger.domain.groupchat.dto.SentMessageDto
import sj.messenger.domain.groupchat.repository.GroupChatRepository
import sj.messenger.domain.groupchat.repository.MessageRepository
import sj.messenger.domain.groupchat.repository.ParticipantRepository
import sj.messenger.domain.user.service.UserService


@Service
@Transactional(readOnly = true)
class GroupChatService(
    private val groupChatRepository: GroupChatRepository,
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

    fun getDirectChat(id: Long): GroupChat {
        return groupChatRepository.findByIdOrNull(id) ?: throw RuntimeException("chat room id '$id' not found")
    }

    @Transactional(readOnly = false)
    fun joinDirectChat(chatRoomId: Long, userId: Long) {
        val chatRoom = findGroupChatWithParticipants(chatRoomId)
        if(chatRoom.isParticipant(userId))
            throw RuntimeException("User(id = ${userId}) is already participant in ChatRoom(id = ${chatRoomId}) ")

        val user = userService.findUserById(userId)
        chatRoom.join(user)
    }

    @Transactional(readOnly = false)
    fun createGroupChat(groupChatCreate: GroupChatCreate): Long {
        val groupChat = GroupChat(name = groupChatCreate.name)
        groupChatRepository.save(groupChat)
        return groupChat.id!!
    }

    fun findUserGroupChats(userId: Long): List<GroupChat>{
        val participants = participantRepository.getParticipantsByUserId(userId)
        return participants.map { it.groupChat }
    }

    fun findGroupChat(chatRoomId: Long): GroupChat{
        return groupChatRepository.findByIdOrNull(chatRoomId) ?: throw RuntimeException("ChatRoom id ${chatRoomId} not found")
    }

    fun findGroupChatWithParticipants(chatRoomId: Long): GroupChat{
        return groupChatRepository.findWithParticipantsById(chatRoomId) ?: throw RuntimeException("ChatRoom id ${chatRoomId} not found")
    }
}