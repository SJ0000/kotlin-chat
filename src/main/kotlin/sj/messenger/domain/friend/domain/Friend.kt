package sj.messenger.domain.friend.domain

import jakarta.persistence.*
import sj.messenger.domain.user.domain.User
import sj.messenger.global.domain.BaseEntity

@Entity
class Friends(
    @ManyToOne(fetch = FetchType.LAZY) var fromUser: User,
    @ManyToOne(fetch = FetchType.LAZY) var toUser: User,

    @Enumerated(value = EnumType.STRING)
    var status: FriendsRequestStatus = FriendsRequestStatus.PENDING,
) : BaseEntity() {

}

enum class FriendsRequestStatus{
    PENDING,
    CONFIRMED,
    REJECTED,
}