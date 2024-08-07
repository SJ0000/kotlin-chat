package sj.messenger.domain.notification.dto

import jakarta.validation.constraints.NotBlank

data class NotificationTokenCreate (
    @field:NotBlank
    val fcmToken: String,
)

data class NotificationTokenUpdate (
    @field:NotBlank
    val fcmToken: String,
)