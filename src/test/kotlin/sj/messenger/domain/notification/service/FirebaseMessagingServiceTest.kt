package sj.messenger.domain.notification.service


import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import sj.messenger.util.randomString


class FirebaseMessagingServiceTest{
    private val firebaseMessagingService = FirebaseMessagingService()

    @Test
    @DisplayName("인자로 전달된 targets 비어있는 경우 RuntimeException 반환")
    fun sendMessageAsyncErrorTest(){

        Assertions.assertThatThrownBy {
            firebaseMessagingService.sendMessageAsync(randomString(10), randomString(10), listOf())
        }.isInstanceOf(RuntimeException::class.java)
    }
}