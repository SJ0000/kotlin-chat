package sj.messenger.domain.chat.domain

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive
import java.time.LocalDateTime

@RedisHash
class Invitation(
    @Id
    val id: String,
    val chatRoomId: Long,
    val inviterId: Long,
    val inviterName: String,
    @TimeToLive
    val timeToLiveSeconds: Long = 7 * 24 * 60 * 60
) {

}