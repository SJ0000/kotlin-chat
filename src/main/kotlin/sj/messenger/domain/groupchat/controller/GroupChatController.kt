package sj.messenger.domain.groupchat.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import sj.messenger.domain.groupchat.dto.GroupChatCreate
import sj.messenger.domain.groupchat.dto.GroupChatDto
import sj.messenger.domain.groupchat.dto.InvitationDto
import sj.messenger.domain.groupchat.service.GroupChatInviteService
import sj.messenger.domain.groupchat.service.GroupChatService
import sj.messenger.domain.security.authentication.principal.LoginUserDetails
import sj.messenger.domain.user.dto.UserDto
import java.net.URI
import java.time.LocalDateTime

@RestController
class GroupChatController(
    private val groupChatService: GroupChatService,
    private val groupChatInviteService: GroupChatInviteService
) {

    @GetMapping("/chats/groups/{id}")
    fun getChatRoomInfo(@PathVariable id: Long): ResponseEntity<GroupChatDto> {
        val chatRoom = groupChatService.findChatRoomWithParticipants(id)
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
        val chatRoomId = groupChatService.createChatRoom(groupChatCreate)
        val chatRoom = groupChatService.findChatRoomWithParticipants(chatRoomId)
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
        val chatRooms = groupChatService.findUserChatRooms(userId)
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
        groupChatService.joinChatRoom(id, userId)
        val chatRoom = groupChatService.findChatRoomWithParticipants(id)
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
        val invitation = groupChatInviteService.createInvitation(userDetails.getUserId(), id)
        val chatRoom = groupChatService.findChatRoom(id)
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
        val invitation = groupChatInviteService.getInvitation(invitationId)
        val chatRoom = groupChatService.getChatRoom(invitation.chatRoomId)
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