package sj.messenger.domain.notification.service


interface NotificationMessagingService {
    fun sendMessageAsync(title: String, content: String, targets: List<String>)
}