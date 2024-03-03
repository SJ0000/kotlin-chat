package sj.messenger.domain.groupchat.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import sj.messenger.domain.groupchat.dto.GroupChatCreate
import sj.messenger.domain.groupchat.dto.GroupChatDto
import sj.messenger.domain.groupchat.dto.InvitationDto
import sj.messenger.domain.groupchat.service.ChatInviteService
import sj.messenger.domain.groupchat.service.ChatService
import sj.messenger.domain.security.authentication.principal.LoginUserDetails
import sj.messenger.domain.user.dto.UserDto
import java.net.URI
import java.time.LocalDateTime

@RestController
class ChatController(
    private val chatService: ChatService,
    private val chatInviteService: ChatInviteService
) {

    @GetMapping("/chats/groups/{id}")
    fun getChatRoomInfo(@PathVariable id: Long): ResponseEntity<GroupChatDto> {
        val chatRoom = chatService.getChatRoom(id)
        val data = GroupChatDto(
            id = chatRoom.id!!,
            name = chatRoom.name,
            avatarUrl = chatRoom.avatarUrl,
            users = chatRoom.participants.map { UserDto(it.user) })

        return ResponseEntity.ok()
            .body(data)
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/chats/groups")
    fun postChatRoom(
        @AuthenticationPrincipal userDetails: LoginUserDetails,
        @RequestBody groupChatCreate: GroupChatCreate,
    ): ResponseEntity<GroupChatDto> {
        val chatRoomId = chatService.createChatRoom(groupChatCreate)
        val chatRoom = chatService.findChatRoomWithParticipants(chatRoomId)
        val data = GroupChatDto(
            id = chatRoom.id!!,
            name = chatRoom.name,
            avatarUrl = chatRoom.avatarUrl,
            users = chatRoom.participants.map { UserDto(it.user) })
        return ResponseEntity.created(URI.create("/chatrooms/${chatRoomId}"))
            .body(data)
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/chats/groups/me")
    fun getMyChatRooms(
        @AuthenticationPrincipal userDetails: LoginUserDetails
    ): ResponseEntity<List<GroupChatDto>> {
        val userId = userDetails.getUserId()
        val chatRooms = chatService.findUserChatRooms(userId)
            .map {
                GroupChatDto(
                    id = it.id!!,
                    name = it.name,
                    avatarUrl = it.avatarUrl,
                    users = it.participants.map { UserDto(it.user) })
            }


        return ResponseEntity.ok(chatRooms)
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/chats/groups/{id}/join")
    fun joinChatRoom(
        @AuthenticationPrincipal userDetails: LoginUserDetails,
        @PathVariable id: Long,
    ): ResponseEntity<GroupChatDto> {
        val userId = userDetails.getUserId()
        chatService.joinChatRoom(id, userId)
        val chatRoom = chatService.findChatRoomWithParticipants(id)
        val dto = GroupChatDto(
            id = chatRoom.id!!,
            name = chatRoom.name,
            avatarUrl = chatRoom.avatarUrl,
            users = chatRoom.participants.map { UserDto(it.user) })
        return ResponseEntity.created(URI.create("/chatrooms/${id}"))
            .body(dto)
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/chats/groups/{id}/invites")
    fun postInviteChatroom(
        @AuthenticationPrincipal userDetails: LoginUserDetails,
        @PathVariable id: Long,
    ): ResponseEntity<InvitationDto> {
        val invitation = chatInviteService.createInvitation(userDetails.getUserId(), id)
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
    @GetMapping("/chats/groups/invites/{invitationId}")
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