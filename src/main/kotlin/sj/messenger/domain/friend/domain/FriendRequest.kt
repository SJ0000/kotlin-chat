package sj.messenger.domain.friend.domain

import jakarta.persistence.*
import sj.messenger.domain.user.domain.User
import sj.messenger.global.domain.BaseEntity

@Entity
class FriendRequest(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val sender: User,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val receiver: User,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
): BaseEntity() {
    var approved: Boolean = false
        protected set;

    fun isReceiver(userId: Long) : Boolean{
        return receiver.id == userId
    }

    fun approve(){
        if(approved)
            throw RuntimeException("FriendRequest already approved.")
        approved = true
    }
}