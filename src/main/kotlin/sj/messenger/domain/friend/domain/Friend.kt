package sj.messenger.domain.friend.domain

import jakarta.persistence.*
import sj.messenger.domain.user.domain.User
import sj.messenger.global.domain.BaseEntity

@Entity
class Friend(
    @ManyToOne(fetch = FetchType.LAZY)
    val fromUser: User,
    @ManyToOne(fetch = FetchType.LAZY)
    val toUser: User,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
): BaseEntity() {
    @Enumerated(value = EnumType.STRING)
    final var status: FriendStatus = FriendStatus.PENDING
        private set;

    fun isReceiver(userId: Long) : Boolean{
        return toUser.id == userId
    }

    // 재요청
    fun reRequest(){
        if(this.status == FriendStatus.REJECTED)
            this.status = FriendStatus.PENDING
        else{
            throw RuntimeException("Only rejected rerequests are allowed.")
        }
    }

    fun approve(){
        if(this.status == FriendStatus.PENDING)
            this.status = FriendStatus.APPROVED
        else{
            throw RuntimeException("Only pending rerequests are allowed.")
        }
    }
}

enum class FriendStatus {
    PENDING,
    APPROVED,
    REJECTED,
}