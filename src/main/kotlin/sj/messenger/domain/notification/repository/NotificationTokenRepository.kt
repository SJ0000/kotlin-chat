package sj.messenger.domain.notification.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import sj.messenger.domain.notification.domain.NotificationToken


interface NotificationTokenRepository : JpaRepository<NotificationToken, Long> {

    @Query("select n from NotificationToken n where n.user.id = :userId")
    fun findByUserId(userId: Long): List<NotificationToken>

    @Query("select n from NotificationToken n where n.user.id in :userIds")
    fun findAllByUserIds(userIds: Iterable<Long>) : List<NotificationToken>
}