package sj.messenger.domain.notification.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import sj.messenger.domain.notification.domain.NotificationToken
import sj.messenger.domain.user.domain.User


interface NotificationTokenRepository : JpaRepository<NotificationToken, Long> {

    @Query("select n from NotificationToken n where n.user.id = :userId")
    fun findByUserId(userId: Long): List<NotificationToken>

}