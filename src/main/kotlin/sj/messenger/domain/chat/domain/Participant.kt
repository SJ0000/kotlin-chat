package sj.messenger.domain.chat.domain

import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne
import sj.messenger.domain.user.domain.User
import sj.messenger.global.domain.BaseEntity

@Entity
class Participant(
    @ManyToOne
    val user: User,
    @ManyToOne
    val chatRoom: ChatRoom
) : BaseEntity() {

}