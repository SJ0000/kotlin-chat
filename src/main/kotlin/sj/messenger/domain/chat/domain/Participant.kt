package sj.messenger.domain.chat.domain

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import sj.messenger.domain.user.domain.User
import sj.messenger.global.domain.BaseEntity

@Entity
class Participant(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    val user: User,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="chatroom_id")
    val chatRoom: ChatRoom
) : BaseEntity() {

}