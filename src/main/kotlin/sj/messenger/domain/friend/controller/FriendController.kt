package sj.messenger.domain.friend.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import sj.messenger.domain.friend.dto.FriendRequestDto
import sj.messenger.domain.friend.dto.ReceivedFriendRequestDto
import sj.messenger.domain.friend.service.FriendService
import sj.messenger.domain.security.authentication.principal.LoginUserDetails
import sj.messenger.domain.user.dto.UserDto

@RestController
class FriendController(
    private val friendService: FriendService,
) {

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/friends")
    fun getFriends(
        @AuthenticationPrincipal userDetails: LoginUserDetails
    ): ResponseEntity<List<UserDto>> {
        val friends = friendService.getFriends(userDetails.getUserId())
        val data = friends.map { UserDto(it) }
        return ResponseEntity.ok(data)
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/friends/requests")
    fun getFriendRequests(
        @AuthenticationPrincipal userDetails: LoginUserDetails
    ): ResponseEntity<List<ReceivedFriendRequestDto>> {
        val requests = friendService.getReceivedRequests(userDetails.getUserId()).map {
            ReceivedFriendRequestDto(it.id!!, UserDto(it.sender), it.createdAt)
        }
        return ResponseEntity.ok(requests)
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/friends")
    fun postFriends(
        @AuthenticationPrincipal userDetails: LoginUserDetails,
        @RequestBody dto: FriendRequestDto
    ) : ResponseEntity<Unit> {
        friendService.request(userDetails.getUserId(), dto.publicIdentifier)

        return ResponseEntity.status(HttpStatus.CREATED)
            .build()
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/friends/requests/{id}/approve")
    fun patchFriends(
        @AuthenticationPrincipal userDetails: LoginUserDetails,
        @PathVariable id: Long,
    ) : ResponseEntity<Unit> {
        friendService.approveRequest(userDetails.getUserId(), id)
        return ResponseEntity.ok().build()
    }

}