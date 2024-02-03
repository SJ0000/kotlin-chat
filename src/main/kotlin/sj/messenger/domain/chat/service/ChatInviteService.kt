package sj.messenger.domain.chat.service

import org.springframework.stereotype.Service
import sj.messenger.domain.chat.domain.Invitation
import sj.messenger.domain.chat.repository.ChatRoomRepository
import java.time.LocalDateTime

@Service
class ChatInviteService(
    private val chatRoomRepository: ChatRoomRepository,
) {
    // TODO : Redis로 변경
    private val invitationRepository: MutableMap<String, Invitation> = mutableMapOf()

    fun createInvitation(userId: Long, chatRoomId: Long): Invitation {
        val chatRoom = chatRoomRepository.findWithParticipantsById(chatRoomId)
            ?: throw RuntimeException("ChatRoom id ${chatRoomId} not found")

        if (!chatRoom.isParticipant(userId))
            throw RuntimeException("User(id = ${userId}) is not participant in ChatRoom(id = ${chatRoomId})")

        val key = generateRandomString()
        val invitation = Invitation(
            key = key,
            chatRoomId = chatRoomId,
            inviterId = userId,
            expiredAt = LocalDateTime.now().plusDays(7)
        )
        invitationRepository[key] = invitation
        return invitation
    }

    private fun generateRandomString(): String {
        val chars = ('a'..'z') + ('A'..'Z')
        return (1..8).map { chars.random() }
            .joinToString("")
    }
}