package sj.messenger.domain.directchat.repository

interface DirectChatRepositoryCustom {

    fun existsByUserIds(userIds : Pair<Long,Long>) : Boolean
}