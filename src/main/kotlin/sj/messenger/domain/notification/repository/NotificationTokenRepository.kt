package sj.messenger.domain.notification.repository

import org.springframework.data.jpa.repository.JpaRepository
import sj.messenger.domain.notification.domain.NotificationToken


interface NotificationTokenRepository : JpaRepository<NotificationToken, Long>{
}