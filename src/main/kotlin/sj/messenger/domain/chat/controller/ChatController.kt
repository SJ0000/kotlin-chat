package sj.messenger.domain.chat.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController
import sj.messenger.domain.chat.domain.ChatRoom
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
        val users = chatRoom.participants.map { UserDto(it.user) }
        val chatRoomDto = ChatRoomDto(id = chatRoom.id!!, users = users)

        return ResponseEntity.ok()
            .body(chatRoomDto)
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/chatrooms")
    fun postChatRoom(@AuthenticationPrincipal userDetails: LoginUserDetails): ResponseEntity<ChatRoomDto> {
        val chatRoomId = chatService.createChatRoom()
        return ResponseEntity.created(URI.create("/chatrooms/${chatRoomId}"))
            .body(ChatRoomDto(chatRoomId))
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/chatrooms/me")
    fun getMyChatRooms(
        @AuthenticationPrincipal userDetails: LoginUserDetails
    ): ResponseEntity<List<ChatRoomDto>> {
        val userId = userDetails.getUserId()
        val data = chatService.findUserChatRooms(userId)
            .map { ChatRoomDto(it.id!!) }
        return ResponseEntity.ok(data)
    }
}