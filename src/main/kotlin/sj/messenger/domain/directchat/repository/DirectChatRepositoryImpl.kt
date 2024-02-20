package sj.messenger.domain.directchat.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.Expressions.*
import com.querydsl.jpa.impl.JPAQueryFactory
import sj.messenger.domain.directchat.domain.QDirectChat.directChat

class DirectChatRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : DirectChatRepositoryCustom {

    override fun existsByUserIds(userIds : Pair<Long,Long>): Boolean {
        return queryFactory
            .select(directChat)
            .from(directChat)
            .where(
                anyOf(
                    directChat.user1.id.eq(userIds.first).and(directChat.user2.id.eq(userIds.second)),
                    directChat.user1.id.eq(userIds.second).and(directChat.user2.id.eq(userIds.first)),
                )
            )
            .fetchFirst() != null
    }
}