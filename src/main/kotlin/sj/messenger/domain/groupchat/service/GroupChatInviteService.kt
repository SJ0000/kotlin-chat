package sj.messenger.domain.groupchat.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import sj.messenger.domain.groupchat.domain.Invitation
import sj.messenger.domain.groupchat.repository.GroupChatRepository
import sj.messenger.domain.groupchat.repository.InvitationRepository
import sj.messenger.domain.user.service.UserService

@Service
class GroupChatInviteService(
    private val userService: UserService,
    private val groupChatRepository: GroupChatRepository,
    private val invitationRepository: InvitationRepository
) {

    fun createInvitation(userId: Long, chatRoomId: Long): Invitation {
        val chatRoom = groupChatRepository.findWithParticipantsById(chatRoomId)
            ?: throw RuntimeException("ChatRoom id ${chatRoomId} not found")

        if (!chatRoom.isParticipant(userId))
            throw RuntimeException("User(id = ${userId}) is not participant in ChatRoom(id = ${chatRoomId})")

        val inviter = userService.findUserById(userId)
        val key = generateRandomString() // 중복 여부 확인
        val invitation = Invitation(
            id = key,
            groupChatId = chatRoomId,
            inviterId = userId,
            inviterName = inviter.name,
        )
        invitationRepository.save(invitation)
        return invitation
    }

    fun getInvitation(id: String) : Invitation{
        return invitationRepository.findByIdOrNull(id) ?: throw RuntimeException("Invitation not found. id = ${id}")
    }

    // 경우의 수 : (대문자 수 26+ 소문자 수 26)^8 = 53,459,728,531,456
    private fun generateRandomString(): String {
        val chars = ('a'..'z') + ('A'..'Z')
        return (1..8).map { chars.random() }
            .joinToString("")
    }
}