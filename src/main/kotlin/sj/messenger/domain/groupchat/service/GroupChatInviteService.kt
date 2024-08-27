package sj.messenger.domain.groupchat.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import sj.messenger.domain.groupchat.domain.GroupChat
import sj.messenger.domain.groupchat.domain.Invitation
import sj.messenger.domain.groupchat.repository.GroupChatRepository
import sj.messenger.domain.groupchat.repository.InvitationRepository
import sj.messenger.domain.user.service.UserService
import sj.messenger.global.exception.EntityNotFoundException

@Service
class GroupChatInviteService(
    private val userService: UserService,
    private val groupChatRepository: GroupChatRepository,
    private val invitationRepository: InvitationRepository
) {

    fun createInvitation(userId: Long, groupChatId: Long): Invitation {
        val chatRoom = groupChatRepository.findWithParticipantsById(groupChatId)
            ?: throw EntityNotFoundException(GroupChat::class, "id", groupChatId)

        if (!chatRoom.isParticipant(userId))
            throw RuntimeException("User(id = ${userId}) is not participant in ChatRoom(id = ${groupChatId})")

        val inviter = userService.findUserById(userId)
        val key = generateRandomString() // 중복 여부 확인
        val invitation = Invitation(
            id = key,
            groupChatId = groupChatId,
            inviterId = userId,
            inviterName = inviter.name,
        )
        invitationRepository.save(invitation)
        return invitation
    }

    fun getInvitation(id: String) : Invitation{
        return invitationRepository.findByIdOrNull(id) ?: throw EntityNotFoundException(Invitation::class,"id", id)
    }

    // 경우의 수 : (대문자 수 26+ 소문자 수 26)^8 = 53,459,728,531,456
    private fun generateRandomString(): String {
        val chars = ('a'..'z') + ('A'..'Z')
        return (1..8).map { chars.random() }
            .joinToString("")
    }
}