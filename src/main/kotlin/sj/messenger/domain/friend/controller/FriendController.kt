package sj.messenger.domain.friend.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import sj.messenger.domain.friend.dto.FriendRequestDto
import sj.messenger.domain.friend.service.FriendService
import sj.messenger.domain.security.authentication.principal.LoginUserDetails

@RestController
class FriendController(
    private val friendService: FriendService,
) {
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/friends")
    fun getFriends(
        @AuthenticationPrincipal userDetails: LoginUserDetails
    ): ResponseEntity<Any> {
        friendService.getReceivedRequests(userDetails.getUserId()).map{

        }
        return ResponseEntity.ok().build()
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/friends")
    fun postFriends(
        @AuthenticationPrincipal userDetails: LoginUserDetails,
        @RequestBody dto: FriendRequestDto
    ) {
        friendService.request(userDetails.getUserId(), dto.recipient)
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/friends/{id}/approve")
    fun patchFriends(
        @AuthenticationPrincipal userDetails: LoginUserDetails,
        @PathVariable id: Long,
    ) {
        friendService.approveRequest(userDetails.getUserId(), id)
    }

}