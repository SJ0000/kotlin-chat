package sj.messenger.domain.notification.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import sj.messenger.domain.notification.dto.NotificationTokenCreate
import sj.messenger.domain.notification.service.NotificationService
import sj.messenger.domain.security.authentication.principal.LoginUserDetails


@RestController
class NotificationController (
    private val notificationService: NotificationService
){

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/notifications/tokens")
    fun postNotificationToken(
        @AuthenticationPrincipal userDetails: LoginUserDetails,
        @Valid @RequestBody tokenCreate : NotificationTokenCreate
    ) : ResponseEntity<Void>{

        notificationService.createToken(userDetails.getUserId(), tokenCreate.fcmToken)
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}