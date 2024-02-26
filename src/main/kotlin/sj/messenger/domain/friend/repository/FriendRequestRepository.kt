package sj.messenger.domain.friend.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import sj.messenger.domain.friend.domain.FriendRequest

interface FriendRequestRepository : JpaRepository<FriendRequest, Long?>{

    @Query("select f from FriendRequest f where f.sender.id = :senderId and f.receiver.id = :receiverId")
    fun findByFromTo(senderId: Long, receiverId: Long) : FriendRequest?

    @Query("select f from FriendRequest f join fetch f.sender where f.receiver.id = :receiverId")
    fun findReceivedAllWithSender(receiverId: Long) : List<FriendRequest>

}