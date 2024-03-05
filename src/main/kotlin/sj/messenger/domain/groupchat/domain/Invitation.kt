package sj.messenger.domain.groupchat.domain

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive

@RedisHash
class Invitation(
    @Id
    val id: String,
    val groupChatId: Long,
    val inviterId: Long,
    val inviterName: String,
    @TimeToLive
    val timeToLiveSeconds: Long = 7 * 24 * 60 * 60
) {

}