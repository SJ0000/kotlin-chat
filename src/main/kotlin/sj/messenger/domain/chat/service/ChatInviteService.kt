package sj.messenger.domain.chat.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import sj.messenger.domain.chat.domain.Invitation
import sj.messenger.domain.chat.repository.ChatRoomRepository
import sj.messenger.domain.chat.repository.InvitationRepository
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.domain.user.service.UserService
import java.time.LocalDateTime

@Service
class ChatInviteService(
    private val chatRoomRepository: ChatRoomRepository,
    private val invitationRepository: InvitationRepository
) {

    fun createInvitation(userId: Long, userName: String, chatRoomId: Long): Invitation {
        val chatRoom = chatRoomRepository.findWithParticipantsById(chatRoomId)
            ?: throw RuntimeException("ChatRoom id ${chatRoomId} not found")

        if (!chatRoom.isParticipant(userId))
            throw RuntimeException("User(id = ${userId}) is not participant in ChatRoom(id = ${chatRoomId})")

        val key = generateRandomString() // 중복 여부 확인
        val invitation = Invitation(
            id = key,
            chatRoomId = chatRoomId,
            inviterId = userId,
            inviterName = userName,
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