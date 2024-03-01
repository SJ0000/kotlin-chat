package sj.messenger.domain.friend.repository

import com.querydsl.core.types.dsl.Expressions.anyOf
import com.querydsl.jpa.impl.JPAQueryFactory
import sj.messenger.domain.friend.domain.Friend
import sj.messenger.domain.friend.domain.QFriend.friend

class FriendRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : FriendRepositoryCustom {

    override fun exists(userId1: Long, userId2: Long): Boolean {
        return queryFactory.select(friend)
            .from(friend)
            .where(
                anyOf(
                    friend.user1.id.eq(userId1)
                        .and(friend.user2.id.eq(userId2)),
                    friend.user1.id.eq(userId2)
                        .and(friend.user2.id.eq(userId1)),
                )
            )
            .fetchFirst() != null
    }

    override fun findAll(userId: Long): List<Friend> {
        return queryFactory.select(friend)
            .from(friend)
            .where(
                friend.user1.id.eq(userId).or(
                    friend.user2.id.eq(userId)
                )
            )
            .fetch()
    }
}