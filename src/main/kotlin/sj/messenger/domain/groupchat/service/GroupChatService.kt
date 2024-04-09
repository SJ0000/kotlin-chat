package sj.messenger.domain.groupchat.service

import io.micrometer.core.annotation.Timed
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sj.messenger.domain.groupchat.domain.GroupChat
import sj.messenger.domain.groupchat.domain.GroupMessage
import sj.messenger.domain.groupchat.dto.GroupChatCreateDto
import sj.messenger.domain.groupchat.dto.SentGroupMessageDto
import sj.messenger.domain.groupchat.repository.GroupChatRepository
import sj.messenger.domain.groupchat.repository.GroupMessageRepository
import sj.messenger.domain.groupchat.repository.ParticipantRepository
import sj.messenger.domain.user.service.UserService


@Service
@Transactional(readOnly = true)
class GroupChatService(
    private val groupChatRepository: GroupChatRepository,
    private val participantRepository: ParticipantRepository,
    private val groupMessageRepository: GroupMessageRepository,

    private val userService: UserService,
) {
    @Timed("service.group-chat.save-message")
    @Transactional(readOnly = false)
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

    fun getDirectChat(id: Long): GroupChat {
        return groupChatRepository.findByIdOrNull(id) ?: throw RuntimeException("chat room id '$id' not found")
    }

    @Transactional(readOnly = false)
    fun joinGroupChat(groupChatId: Long, userId: Long) {
        val chatRoom = findGroupChatWithParticipants(groupChatId)
        if(chatRoom.isParticipant(userId))
            throw RuntimeException("User(id = ${userId}) is already participant in ChatRoom(id = ${groupChatId}) ")

        val user = userService.findUserById(userId)
        chatRoom.join(user)
    }

    @Transactional(readOnly = false)
    fun createGroupChat(groupChatCreateDto: GroupChatCreateDto): Long {
        val groupChat = GroupChat(name = groupChatCreateDto.name)
        groupChatRepository.save(groupChat)
        return groupChat.id!!
    }

    fun findUserGroupChats(userId: Long): List<GroupChat>{
        val participants = participantRepository.getParticipantsWithGroupChatByUserId(userId)
        return participants.map { it.groupChat }
    }

    fun findGroupChat(chatRoomId: Long): GroupChat{
        return groupChatRepository.findByIdOrNull(chatRoomId) ?: throw RuntimeException("ChatRoom id ${chatRoomId} not found")
    }

    fun findGroupChatWithParticipants(chatRoomId: Long): GroupChat{
        return groupChatRepository.findWithParticipantsById(chatRoomId) ?: throw RuntimeException("ChatRoom id ${chatRoomId} not found")
    }
}