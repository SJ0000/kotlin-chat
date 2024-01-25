package sj.messenger.domain.chat.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import sj.messenger.domain.chat.domain.ChatRoom
import sj.messenger.domain.chat.dto.ChatRoomCreate
import sj.messenger.domain.chat.dto.ChatRoomDto
import sj.messenger.domain.chat.service.ChatService
import sj.messenger.domain.security.authentication.principal.LoginUserDetails
import sj.messenger.domain.user.dto.UserDto
import java.net.URI
import java.security.Principal

@RestController
class ChatController(
    private val chatService: ChatService,
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
        val chatRoom = chatService.findChatRoom(chatRoomId)
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
            .map { ChatRoomDto(
                id = it.id!!,
                name = it.name,
                avatarUrl = it.avatarUrl,
                users = it.participants.map { UserDto(it.user) }) }

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
        return ResponseEntity.created(URI.create("/chatrooms/${id}"))
            .build()
    }
}