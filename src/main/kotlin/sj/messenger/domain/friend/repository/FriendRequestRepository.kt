package sj.messenger.domain.friend.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import sj.messenger.domain.friend.domain.FriendRequest

interface FriendRequestRepository : JpaRepository<FriendRequest, Long?>{

    @Query("select f from FriendRequest f where f.fromUser.id = :fromUserId and f.toUser.id = :toUserId")
    fun findByFromTo(fromUserId: Long, toUserId: Long) : FriendRequest?

    @Query("select f from FriendRequest f join fetch f.fromUser where f.toUser.id = :receiverId")
    fun findReceivedAllWithSender(receiverId: Long) : List<FriendRequest>

}