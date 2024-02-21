package sj.messenger.domain.directchat.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import sj.messenger.domain.directchat.domain.DirectChat

interface DirectChatRepository : JpaRepository<DirectChat, Long?>, DirectChatRepositoryCustom {

    @Query("select d from DirectChat d join fetch d.user1 join fetch d.user2 where d.user1.id = :userId or d.user2.id = :userId")
    fun findAllByUserIdWithUsers(userId: Long) : List<DirectChat>

    @Query("select d from DirectChat d join fetch d.user1 join fetch d.user2 where d.id = :id")
    fun findByIdWithUsers(id: Long) : DirectChat?
}