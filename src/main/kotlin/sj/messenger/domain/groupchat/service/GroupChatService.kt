package sj.messenger.domain.groupchat.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sj.messenger.domain.groupchat.domain.GroupChat
import sj.messenger.domain.groupchat.dto.GroupChatCreateDto
import sj.messenger.domain.groupchat.dto.GroupChatDto
import sj.messenger.domain.groupchat.dto.ParticipantDto
import sj.messenger.domain.groupchat.dto.ParticipantUpdateDto
import sj.messenger.domain.groupchat.repository.GroupChatRepository
import sj.messenger.domain.groupchat.repository.ParticipantRepository
import sj.messenger.domain.user.dto.UserDto
import sj.messenger.domain.user.service.UserService


@Service
@Transactional(readOnly = true)
class GroupChatService(
    private val groupChatRepository: GroupChatRepository,
    private val participantRepository: ParticipantRepository,
    private val userService: UserService,
) {

    fun getGroupChat(id: Long): GroupChat {
        return groupChatRepository.findByIdOrNull(id) ?: throw RuntimeException("chat room id '$id' not found")
    }

    @Transactional(readOnly = false)
    fun joinGroupChat(groupChatId: Long, userId: Long) {
        val chatRoom = findGroupChatWithParticipants(groupChatId)
        if (chatRoom.isParticipant(userId))
            throw RuntimeException("User(id = ${userId}) is already participant in ChatRoom(id = ${groupChatId}) ")

        val user = userService.findUserById(userId)
        chatRoom.join(user)
    }

    @Transactional(readOnly = false)
    fun createGroupChat(creatorId: Long, groupChatCreateDto: GroupChatCreateDto): Long {
        val creator = userService.findUserById(creatorId);
        val groupChat = GroupChat.create(creator, groupChatCreateDto.name)
        groupChatRepository.save(groupChat)
        return groupChat.id!!
    }

    fun findUserGroupChats(userId: Long): List<GroupChat> {
        val participants = participantRepository.getParticipantsWithGroupChatByUserId(userId)
        return participants.map { it.groupChat }
    }

    fun findGroupChat(groupChatId: Long): GroupChat {
        return groupChatRepository.findByIdOrNull(groupChatId)
            ?: throw RuntimeException("ChatRoom id ${groupChatId} not found")
    }

    fun getGroupChatWithUsers(groupChatId: Long): GroupChatDto {
        val groupChat = findGroupChatWithParticipants(groupChatId)
        val userIds = groupChat.getParticipantUserIds()
        val users = userService.findUsers(userIds).associateBy { it.id };

        return GroupChatDto(
            id = groupChat.id!!,
            name = groupChat.name,
            avatarUrl = groupChat.avatarUrl,
            participants = groupChat.participants.map {
                ParticipantDto(
                    id = it.id!!,
                    user = UserDto(users[it.user.id]!!),
                    role = it.role
                )
            })
    }

    @Transactional(readOnly = false)
    fun updateParticipant(groupChatId: Long, modifierUserId: Long, updateDto: ParticipantUpdateDto){
        val modifier = participantRepository.findByGroupChatIdAndUserId(groupChatId, modifierUserId)
        val target = participantRepository.findByIdOrNull(updateDto.participantId) ?: throw RuntimeException("participant not exists. id = ${updateDto.participantId}")

        if(!modifier.canModify(target))
            throw RuntimeException("Participant(id = ${modifier.id})Can Edit Target Participant(id = ${target.id}).")

        target.role = updateDto.role
    }

    fun findGroupChatWithParticipants(groupChatId: Long): GroupChat {
        return groupChatRepository.findWithParticipantsById(groupChatId)
            ?: throw RuntimeException("ChatRoom id ${groupChatId} not found")
    }
}