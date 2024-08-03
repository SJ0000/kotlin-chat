package sj.messenger.domain.notification.service

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.MulticastMessage
import com.google.firebase.messaging.Notification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sj.messenger.domain.directchat.service.DirectChatService
import sj.messenger.domain.groupchat.service.GroupChatService
import sj.messenger.domain.notification.domain.NotificationToken
import sj.messenger.domain.notification.repository.NotificationTokenRepository
import sj.messenger.domain.user.service.UserService

@Service
@Transactional(readOnly = false)
class NotificationService(
    private val notificationTokenRepository: NotificationTokenRepository,
    private val userService: UserService,
    private val directChatService: DirectChatService,
    private val groupChatService: GroupChatService
) {

    fun createToken(userId: Long, token: String) {
        val user = userService.findUserById(userId)
        notificationTokenRepository.save(NotificationToken(user, token))
    }

    fun sendDirectNotification(senderId: Long, directChatId: Long, content: String) {
        val directChat = directChatService.getDirectChat(senderId, directChatId)
        val target = directChat.getOtherUser(senderId)
        val sender = directChat.getOtherUser(target.id!!)

        val notificationTokens = notificationTokenRepository.findByUserId(target.id!!)
        val fcmTokens = extractFcmTokens(notificationTokens);

        val notification = createNotification(sender.name, content)
        val fcmMessage = createFcmMessage(notification, fcmTokens)
        FirebaseMessaging.getInstance().sendEachForMulticastAsync(fcmMessage)
    }

    fun sendGroupNotification(senderId: Long, groupChatId: Long, content: String){
        val groupChat = groupChatService.findGroupChatWithParticipants(groupChatId)
        val targetUserIds = groupChat.getParticipantUserIds().filter { it != senderId }

        val notificationTokens = notificationTokenRepository.findAllByUserIds(targetUserIds)
        val fcmTokens = extractFcmTokens(notificationTokens)

        val notification = createNotification(groupChat.name, content)
        val fcmMessage = createFcmMessage(notification, fcmTokens)
        FirebaseMessaging.getInstance().sendEachForMulticastAsync(fcmMessage)
    }


    private fun extractFcmTokens(notificationTokens: List<NotificationToken>): List<String> {
        return notificationTokens.map { it.fcmToken }
    }

    private fun createNotification(title: String, body: String): Notification {
        return Notification.builder()
            .setTitle(title)
            .setBody(body)
            .build();
    }

    private fun createFcmMessage(notification: Notification, fcmTokens: List<String>): MulticastMessage {
        return MulticastMessage.builder()
            .setNotification(notification)
            .addAllTokens(fcmTokens)
            .build()
    }
}