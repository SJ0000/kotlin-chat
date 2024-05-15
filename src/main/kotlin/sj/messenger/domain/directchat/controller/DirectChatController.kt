package sj.messenger.domain.directchat.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import sj.messenger.domain.directchat.dto.DirectChatDto
import sj.messenger.domain.directchat.dto.ReceivedDirectMessageDto
import sj.messenger.domain.directchat.service.DirectChatMessageService
import sj.messenger.domain.directchat.service.DirectChatService
import sj.messenger.domain.security.authentication.principal.LoginUserDetails
import sj.messenger.domain.user.dto.UserDto
import java.net.URI
import java.time.LocalDate
import java.time.LocalDateTime

@RestController
class DirectChatController(
    private val directChatService: DirectChatService,
    private val directChatMessageService: DirectChatMessageService,
) {

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/chats/directs/{id}")
    fun getDirectChat(
        @AuthenticationPrincipal userDetails: LoginUserDetails,
        @PathVariable(name = "id") directChatId: Long,
    ): ResponseEntity<DirectChatDto> {
        val directChat = directChatService.getDirectChat(userDetails.getUserId(), directChatId)
        val data = DirectChatDto(
            id = directChat.id!!,
            otherUser = UserDto(directChat.getOtherUser(myId = userDetails.getUserId()))
        )
        return ResponseEntity.ok(data)
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/chats/directs/me")
    fun getMyDirectChats(
        @AuthenticationPrincipal userDetails: LoginUserDetails,
    ): ResponseEntity<List<DirectChatDto>> {
        val directChats = directChatService.getUserDirectChats(userDetails.getUserId())
        val data = directChats.map {
            DirectChatDto(
                id = it.id!!,
                otherUser = UserDto(it.getOtherUser(myId = userDetails.getUserId()))
            )
        }
        return ResponseEntity.ok(data)
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/chats/directs")
    fun postDirectChat(
        @AuthenticationPrincipal userDetails: LoginUserDetails,
        @RequestParam to: Long
    ): ResponseEntity<Long> {
        val directChatId = directChatService.createDirectChat(Pair(userDetails.getUserId(), to))
        return ResponseEntity.created(URI.create("/chats/directs/${directChatId}"))
            .body(directChatId)
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/chats/directs/{id}/messages")
    fun getDirectMessages(
        @PathVariable id: Long,
        @RequestParam(required = false) dateTime: LocalDateTime = LocalDateTime.now()
    ) : ResponseEntity<List<ReceivedDirectMessageDto>> {
        val previousMessages = directChatMessageService.getPreviousMessages(id, dateTime)
        return ResponseEntity.ok(previousMessages)
    }

}