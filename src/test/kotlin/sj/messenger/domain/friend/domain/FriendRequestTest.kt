package sj.messenger.domain.friend.domain

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

import sj.messenger.util.generateUser

class FriendRequestTest {

    @Test
    @DisplayName("인자로 받은 사용자가 친구 요청을 받은 사용자인지 확인")
    fun isReceiver() {
        // given
        val sender = generateUser()
        val receiver = generateUser()
        val friend = FriendRequest(sender, receiver)

        // when
        val isReceiver = friend.isReceiver(receiver.id!!)

        // then
        assertThat(isReceiver).isTrue()
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