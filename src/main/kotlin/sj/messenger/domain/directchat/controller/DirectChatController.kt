package sj.messenger.domain.directchat.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import sj.messenger.domain.directchat.dto.DirectChatDto
import sj.messenger.domain.chat.service.DirectChatService
import sj.messenger.domain.security.authentication.principal.LoginUserDetails

@RestController
class DirectChatController (
    private val directChatService: DirectChatService,
){
    // 나의 개인 대화방 조회
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
    ){
        directChatService.validateExists(userDetails.getUserId(),to)
    }
}