package sj.messenger.domain.friend.repository

import sj.messenger.domain.friend.domain.Friend


// query 정의
interface FriendRepositoryCustom {

    fun findAll(userId: Long): List<Friend>

    fun exists(userId1: Long, userId2: Long) : Boolean
}