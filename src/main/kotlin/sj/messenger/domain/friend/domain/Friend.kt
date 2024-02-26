package sj.messenger.domain.friend.domain

import jakarta.persistence.*
import sj.messenger.domain.user.domain.User
import sj.messenger.global.domain.BaseEntity

@Entity
class Friend(
    @ManyToOne(fetch = FetchType.LAZY)
    val user1: User,
    @ManyToOne(fetch = FetchType.LAZY)
    val user2: User,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
): BaseEntity() {

}
