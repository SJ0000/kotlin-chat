package sj.messenger.domain.groupchat.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import sj.messenger.domain.groupchat.dto.GroupChatCreateDto
import sj.messenger.domain.groupchat.dto.GroupChatDto
import sj.messenger.domain.groupchat.dto.InvitationDto
import sj.messenger.domain.groupchat.service.GroupChatInviteService
import sj.messenger.domain.groupchat.service.GroupChatService
import sj.messenger.domain.security.authentication.principal.LoginUserDetails
import sj.messenger.domain.user.dto.UserDto
import sj.messenger.domain.user.service.UserService
import java.net.URI
import java.time.LocalDateTime

@RestController
class GroupChatController(
    private val groupChatService: GroupChatService,
    private val groupChatInviteService: GroupChatInviteService,
    private val userService: UserService,
) {

    @GetMapping("/chats/groups/{id}")
    fun getGroupChatInfo(@PathVariable id: Long): ResponseEntity<GroupChatDto> {
        val groupChat = groupChatService.findGroupChatWithParticipants(id)
        val userIds = groupChat.getParticipantUserIds()
        val users = userService.findUsers(userIds)
        val data = GroupChatDto(
            id = groupChat.id!!,
            name = groupChat.name,
            avatarUrl = groupChat.avatarUrl,
            users = users.map { UserDto(it) })
        return ResponseEntity.ok()
            .body(data)
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/chats/groups")
    fun postGroupChat(
        @RequestBody groupChatCreateDto: GroupChatCreateDto,
    ): ResponseEntity<GroupChatDto> {
        val groupChatId = groupChatService.createGroupChat(groupChatCreateDto)
        val groupChat = groupChatService.findGroupChatWithParticipants(groupChatId)
        val data = GroupChatDto(
            id = groupChat.id!!,
            name = groupChat.name,
            avatarUrl = groupChat.avatarUrl,
        )
        return ResponseEntity.created(URI.create("/chatrooms/${groupChatId}"))
            .body(data)
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/chats/groups/me")
    fun getMyGroupChats(
        @AuthenticationPrincipal userDetails: LoginUserDetails
    ): ResponseEntity<List<GroupChatDto>> {
        val userId = userDetails.getUserId()
        val groupChats = groupChatService.findUserGroupChats(userId)
            .map {
                GroupChatDto(
                    id = it.id!!,
                    name = it.name,
                    avatarUrl = it.avatarUrl,
                )
            }
        return ResponseEntity.ok(groupChats)
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/chats/groups/{id}/join")
    fun joinGroupChat(
        @AuthenticationPrincipal userDetails: LoginUserDetails,
        @PathVariable id: Long,
    ): ResponseEntity<GroupChatDto> {
        val userId = userDetails.getUserId()
        groupChatService.joinGroupChat(id, userId)
        val groupChat = groupChatService.findGroupChatWithParticipants(id)
        val users = userService.findUsers(groupChat.getParticipantUserIds())
        val dto = GroupChatDto(
            id = groupChat.id!!,
            name = groupChat.name,
            avatarUrl = groupChat.avatarUrl,
            users = users.map { UserDto(it) })
        return ResponseEntity.created(URI.create("/chats/groups/${id}"))
            .body(dto)
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/chats/groups/{id}/invites")
    fun postInviteGroupChat(
        @AuthenticationPrincipal userDetails: LoginUserDetails,
        @PathVariable id: Long,
    ): ResponseEntity<InvitationDto> {
        val invitation = groupChatInviteService.createInvitation(userDetails.getUserId(), id)
        val chatRoom = groupChatService.findGroupChat(id)
        val dto = InvitationDto(
            id = invitation.id,
            groupChatId = invitation.groupChatId,
            groupChatName = chatRoom.name,
            inviterName = invitation.inviterName,
            expiredAt = LocalDateTime.now().plusMinutes(invitation.timeToLiveSeconds)
        )
        return ResponseEntity.ok(dto)
    }

    @GetMapping("/chats/groups/invites/{invitationId}")
    fun getInvitation(
        @PathVariable invitationId: String
    ): ResponseEntity<InvitationDto> {
        val invitation = groupChatInviteService.getInvitation(invitationId)
        val groupChat = groupChatService.getDirectChat(invitation.groupChatId)
        val dto = InvitationDto(
            id = invitation.id,
            groupChatId = invitation.groupChatId,
            groupChatName = groupChat.name,
            inviterName = invitation.inviterName,
            expiredAt = LocalDateTime.now().plusMinutes(invitation.timeToLiveSeconds)
        )
        return ResponseEntity.ok(dto)
    }
}