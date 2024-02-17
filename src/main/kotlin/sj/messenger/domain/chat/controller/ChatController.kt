package sj.messenger.domain.chat.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import sj.messenger.domain.chat.dto.ChatRoomCreate
import sj.messenger.domain.chat.dto.ChatRoomDto
import sj.messenger.domain.chat.dto.InvitationDto
import sj.messenger.domain.chat.service.ChatInviteService
import sj.messenger.domain.chat.service.ChatService
import sj.messenger.domain.security.authentication.principal.LoginUserDetails
import sj.messenger.domain.user.dto.UserDto
import java.net.URI
import java.time.LocalDateTime

@RestController
class ChatController(
    private val chatService: ChatService,
    private val chatInviteService: ChatInviteService
) {

    @GetMapping("/chatrooms/{id}")
    fun getChatRoomInfo(@PathVariable id: Long): ResponseEntity<ChatRoomDto> {
        val chatRoom = chatService.getChatRoom(id)
        val data = ChatRoomDto(
            id = chatRoom.id!!,
            name = chatRoom.name,
            avatarUrl = chatRoom.avatarUrl,
            users = chatRoom.participants.map { UserDto(it.user) })

        return ResponseEntity.ok()
            .body(data)
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/chatrooms")
    fun postChatRoom(
        @AuthenticationPrincipal userDetails: LoginUserDetails,
        @RequestBody chatRoomCreate: ChatRoomCreate,
    ): ResponseEntity<ChatRoomDto> {
        val chatRoomId = chatService.createChatRoom(chatRoomCreate)
        val chatRoom = chatService.findChatRoomWithParticipants(chatRoomId)
        val data = ChatRoomDto(
            id = chatRoom.id!!,
            name = chatRoom.name,
            avatarUrl = chatRoom.avatarUrl,
            users = chatRoom.participants.map { UserDto(it.user) })
        return ResponseEntity.created(URI.create("/chatrooms/${chatRoomId}"))
            .body(data)
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/chatrooms/me")
    fun getMyChatRooms(
        @AuthenticationPrincipal userDetails: LoginUserDetails
    ): ResponseEntity<List<ChatRoomDto>> {
        val userId = userDetails.getUserId()
        val chatRooms = chatService.findUserChatRooms(userId)
            .map {
                ChatRoomDto(
                    id = it.id!!,
                    name = it.name,
                    avatarUrl = it.avatarUrl,
                    users = it.participants.map { UserDto(it.user) })
            }


        return ResponseEntity.ok(chatRooms)
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/chatrooms/{id}/join")
    fun joinChatRoom(
        @AuthenticationPrincipal userDetails: LoginUserDetails,
        @PathVariable id: Long,
    ): ResponseEntity<ChatRoomDto> {
        val userId = userDetails.getUserId()
        chatService.joinChatRoom(id, userId)
        val chatRoom = chatService.findChatRoomWithParticipants(id)
        val dto = ChatRoomDto(
            id = chatRoom.id!!,
            name = chatRoom.name,
            avatarUrl = chatRoom.avatarUrl,
            users = chatRoom.participants.map { UserDto(it.user) })
        return ResponseEntity.created(URI.create("/chatrooms/${id}"))
            .body(dto)
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/chatrooms/{id}/invites")
    fun postInviteChatroom(
        @AuthenticationPrincipal userDetails: LoginUserDetails,
        @PathVariable id: Long,
    ): ResponseEntity<InvitationDto> {
        val invitation = chatInviteService.createInvitation(userDetails.getUserId(), userDetails.username, id)
        val chatRoom = chatService.findChatRoom(id)
        val dto = InvitationDto(
            id = invitation.id,
            chatRoomId = invitation.chatRoomId,
            chatRoomName = chatRoom.name,
            inviterName = invitation.inviterName,
            expiredAt = LocalDateTime.now().plusMinutes(invitation.timeToLiveSeconds)
        )
        return ResponseEntity.ok(dto)
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/chat/invites/{invitationId}")
    fun getInvitation(
        @PathVariable invitationId: String
    ): ResponseEntity<InvitationDto> {
        val invitation = chatInviteService.getInvitation(invitationId)
        val chatRoom = chatService.getChatRoom(invitation.chatRoomId)
        val dto = InvitationDto(
            id = invitation.id,
            chatRoomId = invitation.chatRoomId,
            chatRoomName = chatRoom.name,
            inviterName = invitation.inviterName,
            expiredAt = LocalDateTime.now().plusMinutes(invitation.timeToLiveSeconds)
        )
        return ResponseEntity.ok(dto)
    }
}