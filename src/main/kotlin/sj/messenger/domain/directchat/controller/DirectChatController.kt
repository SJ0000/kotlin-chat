package sj.messenger.domain.directchat.controller

import org.springframework.http.ResponseEntity
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import sj.messenger.domain.directchat.dto.DirectChatDto
import sj.messenger.domain.chat.service.DirectChatService
import sj.messenger.domain.security.authentication.principal.LoginUserDetails
import java.net.URI

@RestController
class DirectChatController (
    private val directChatService: DirectChatService,
){

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/chats/direct/me")
    fun getMyDirectChats(
        @AuthenticationPrincipal userDetails: LoginUserDetails,
    ): ResponseEntity<List<DirectChatDto>>{
        val result = directChatService.getUserDirectChats(userDetails.getUserId())
        return ResponseEntity.ok(result)
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/chats/direct")
    fun postDirectChat(
        @AuthenticationPrincipal userDetails: LoginUserDetails,
        @RequestParam to: Long
    ): ResponseEntity<Long>{
        val directChatId = directChatService.createDirectChat(Pair(userDetails.getUserId(), to))
        return ResponseEntity.created(URI.create("/chats/direct/${directChatId}"))
            .body(directChatId)
    }
}