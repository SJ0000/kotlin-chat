package sj.messenger.domain.friend.domain

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import sj.messenger.util.generateUser

class FriendRequestTest {

    @Test
    @DisplayName("인자로 받은 사용자가 친구 요청을 받은 사용자인지 확인")
    fun isReceiver() {
        // given
        val sender = generateUser(1L)
        val receiver = generateUser(2L)
        val friend = FriendRequest(sender, receiver)

        // then
        assertThat(friend.isReceiver(receiver.id!!)).isTrue()
        assertThat(friend.isReceiver(receiver.id!!+1)).isFalse()
    }

    @Test
    @DisplayName("이미 승인된 요청을 승인시 예외를 반환한다.")
    fun approve() {
        // given
        val sender = generateUser()
        val receiver = generateUser()
        val request = FriendRequest(sender, receiver)
        request.approve()

        // expected
        assertThatThrownBy { request.approve() }
            .isInstanceOf(RuntimeException::class.java)
    }
}