package sj.messenger.domain.friend.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import sj.messenger.domain.friend.domain.Friend

interface FriendRepository : JpaRepository<Friend,Long?>, FriendRepositoryCustom{

    fun findAllByToUserId(toUserId: Long) : List<Friend>

}