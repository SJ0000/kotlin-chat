package sj.messenger.domain.friend.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.Expressions.*
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import sj.messenger.domain.friend.domain.Friend
import sj.messenger.domain.friend.domain.FriendStatus
import sj.messenger.domain.friend.domain.QFriend
import sj.messenger.domain.friend.domain.QFriend.*
import sj.messenger.domain.user.domain.QUser
import sj.messenger.domain.user.domain.QUser.*
import sj.messenger.domain.user.domain.User

class FriendRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : FriendRepositoryCustom {


    override fun findByFromTo(fromUserId: Long, toUserId: Long, status: FriendStatus): Friend? {
        return getSelectQuery(fromUserId, toUserId, status)
            .fetchOne()
    }

    override fun exists(fromUserId: Long, toUserId: Long, status: FriendStatus): Boolean {
        return getSelectQuery(fromUserId, toUserId, status)
            .fetchFirst() != null
    }

    private fun getSelectQuery(fromUserId: Long, toUserId: Long, status: FriendStatus): JPAQuery<Friend> {
        return queryFactory.select(friend)
            .from(friend)
            .where(
                friend.fromUser.id.eq(fromUserId)
                    .and(friend.toUser.id.eq(toUserId))
                    .and(friend.status.eq(status))
            )
    }

    override fun existsIgnoreFromTo(fromUserId: Long, toUserId: Long, status: FriendStatus): Boolean {
        return queryFactory.select(friend)
            .from(friend)
            .where(
                anyOf(
                    friend.fromUser.id.eq(fromUserId)
                        .and(friend.toUser.id.eq(toUserId)),
                    friend.toUser.id.eq(fromUserId)
                        .and(friend.fromUser.id.eq(toUserId)),
                )
                    .and(friend.status.eq(status))
            )
            .fetchFirst() != null
    }

    override fun findApprovedAll(userId: Long): List<Friend> {
        return queryFactory.select(friend)
            .from(friend)
            .where(
                friend.fromUser.id.eq(userId).or(
                    friend.toUser.id.eq(userId)
                )
            )
            .fetch()
    }
}