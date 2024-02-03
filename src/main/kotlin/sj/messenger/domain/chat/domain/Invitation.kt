package sj.messenger.domain.chat.domain

import java.time.LocalDateTime

class Invitation (
    val key: String,
    val chatRoomId : Long,
    val inviterId : Long,
    val expiredAt: LocalDateTime
){

}