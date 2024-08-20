package sj.messenger.domain.notification.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sj.messenger.domain.directchat.service.DirectChatService
import sj.messenger.domain.groupchat.service.GroupChatService
import sj.messenger.domain.notification.domain.NotificationToken
import sj.messenger.domain.notification.repository.NotificationTokenRepository
import sj.messenger.domain.user.service.UserService
import sj.messenger.global.exception.ExpiredFcmTokenException
import sj.messenger.global.exception.FcmTokenAlreadyExistsException
import java.time.LocalDateTime

@Service
@Transactional(readOnly = false)
class NotificationService(
    private val notificationTokenRepository: NotificationTokenRepository,
    private val userService: UserService,
    private val directChatService: DirectChatService,
    private val groupChatService: GroupChatService,
    private val notificationMessagingService: NotificationMessagingService
) {

    @Transactional
    fun registerFcmToken(userId: Long, fcmToken: String, baseTime: LocalDateTime = LocalDateTime.now()) {
        val notificationToken = notificationTokenRepository.findFirstByUserId(userId)
        when {
            notificationToken == null -> {
                val user = userService.findUserById(userId)
                notificationTokenRepository.save(NotificationToken(user, fcmToken))
            }

            notificationToken.fcmToken != fcmToken -> throw FcmTokenAlreadyExistsException()
            notificationToken.isExpired(baseTime) -> throw ExpiredFcmTokenException()
            else -> { /* Do Nothing */
            }
        }
    }

    @Transactional
    fun updateNotificationToken(userId: Long, newFcmToken: String) {
        val notificationToken = notificationTokenRepository.findFirstByUserId(userId)
            ?: throw RuntimeException("Notification token not exists.")
        notificationToken.fcmToken = newFcmToken
    }

    @Transactional
    fun removeUserNotificationToken(userId: Long) {
        val notificationToken = notificationTokenRepository.findFirstByUserId(userId)
        if (notificationToken != null)
            notificationTokenRepository.delete(notificationToken)
    }

    fun sendDirectNotification(senderId: Long, directChatId: Long, content: String) {
        val directChat = directChatService.getDirectChat(senderId, directChatId)
        val sender = directChat.getUser(senderId)
        val fcmTokens = getUsersFcmToken(directChat.getOtherUser(senderId).id!!)
        notificationMessagingService.sendMessageAsync(sender.name, content ,fcmTokens)
    }

    fun sendGroupNotification(senderId: Long, groupChatId: Long, content: String) {
        val groupChat = groupChatService.findGroupChatWithParticipants(groupChatId)
        val targetUserIds = groupChat.getParticipantUserIds().filter { it != senderId }
        val fcmTokens = getUsersFcmToken(*targetUserIds.toLongArray())
        notificationMessagingService.sendMessageAsync(groupChat.name, content, fcmTokens)
    }

    private fun getUsersFcmToken(vararg userIds: Long): List<String> {
        val notificationTokens = notificationTokenRepository.findAllByUserIds(userIds.toList())
        return notificationTokens.map { it.fcmToken }
    }
}