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

    fun approveIfPossible(userId: Long){
        if (receiver.id != userId)
            throw IllegalArgumentException("User(Id = ${userId})는 친구요청을 받은 사용자가 아닙니다.")

        if(approved)
            throw IllegalStateException("친구 요청이 이미 승인된 상태입니다.")

        approved = true
    }
}