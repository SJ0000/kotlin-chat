package sj.messenger.domain.friend.repository

import sj.messenger.domain.friend.domain.Friend
import sj.messenger.domain.friend.domain.FriendStatus


// query 정의
interface FriendRepositoryCustom {

    fun exists(fromUserId: Long, toUserId: Long, status: FriendStatus) : Boolean

    fun existsIgnoreFromTo(fromUserId: Long, toUserId: Long, status: FriendStatus) : Boolean

    fun findByFromTo(fromUserId: Long, toUserId: Long, status: FriendStatus) : Friend?
}