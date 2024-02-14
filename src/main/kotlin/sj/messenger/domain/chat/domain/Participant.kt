package sj.messenger.domain.chat.domain

import jakarta.persistence.*
import sj.messenger.domain.user.domain.User
import sj.messenger.global.domain.BaseEntity

@Entity
class Participant(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    val user: User,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="chatroom_id")
    val chatRoom: ChatRoom,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long? = null
) {

}