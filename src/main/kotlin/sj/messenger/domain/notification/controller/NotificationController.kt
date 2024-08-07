package sj.messenger.domain.notification.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import sj.messenger.domain.notification.dto.NotificationTokenCreate
import sj.messenger.domain.notification.dto.NotificationTokenUpdate
import sj.messenger.domain.notification.service.NotificationService
import sj.messenger.domain.security.authentication.principal.LoginUserDetails


@RestController
class NotificationController(
    private val notificationService: NotificationService
) {

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/notifications/tokens")
    fun postNotificationToken(
        @AuthenticationPrincipal userDetails: LoginUserDetails,
        @Valid @RequestBody tokenCreate: NotificationTokenCreate
    ): ResponseEntity<Void> {
        notificationService.createTokenIfNotExists(userDetails.getUserId(), tokenCreate.fcmToken)
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/notifications/tokens")
    fun patchNotificationToken(
        @AuthenticationPrincipal userDetails: LoginUserDetails,
        @Valid @RequestBody tokenUpdate: NotificationTokenUpdate
    ): ResponseEntity<Void> {
        notificationService.updateNotificationToken(userDetails.getUserId(), tokenUpdate.fcmToken)
        return ResponseEntity.ok().build()
    }

    /**
     * Client 로그아웃 전 호출하는 API
     * 현재 1인당 1개의 기기만 허용하기 때문에, 토큰 이름을 받을 필요 없음.
     */
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/notifications/tokens")
    fun deleteNotificationToken(
        @AuthenticationPrincipal userDetails: LoginUserDetails
    ):ResponseEntity<Void> {
        notificationService.removeUserNotificationToken(userDetails.getUserId())
        return ResponseEntity.noContent().build()
    }
}