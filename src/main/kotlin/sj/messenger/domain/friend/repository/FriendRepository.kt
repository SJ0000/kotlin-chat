package sj.messenger.domain.friend.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import sj.messenger.domain.friend.domain.Friend
import sj.messenger.domain.friend.domain.FriendStatus

interface FriendRepository : JpaRepository<Friend,Long?>, FriendRepositoryCustom{

    @Query("select f from Friend f join fetch f.fromUser where f.toUser.id = :toUserId and f.status = :status")
    fun findAllByToUserIdWithFromUser(toUserId: Long, status: FriendStatus) : List<Friend>

}