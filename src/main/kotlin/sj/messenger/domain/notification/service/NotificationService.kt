package sj.messenger.domain.notification.service

import org.springframework.stereotype.Service
import sj.messenger.domain.notification.domain.NotificationToken
import sj.messenger.domain.notification.repository.NotificationTokenRepository
import sj.messenger.domain.user.service.UserService

@Service
class NotificationService (
    private val notificationTokenRepository: NotificationTokenRepository,
    private val userService: UserService,
){

    fun createToken(userId: Long, token: String){
        val user = userService.findUserById(userId)
        notificationTokenRepository.save(NotificationToken(user,token))
    }
}