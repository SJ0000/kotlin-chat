package sj.messenger.domain.friend.domain

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

import org.springframework.test.util.ReflectionTestUtils
import sj.messenger.util.generateUser

class FriendTest {

    @Test
    @DisplayName("인자로 받은 사용자가 친구 요청을 받은 사용자인지 확인")
    fun isReceiver() {
        // given
        val sender = generateUser()
        val receiver = generateUser()
        val friend = Friend(sender, receiver)

        // when
        val isReceiver = friend.isReceiver(receiver.id!!)

        // then
        assertThat(isReceiver).isTrue()
    }

    @Test
    @DisplayName("Rejected 요청을 재요청시 Pending 상태로 변경된다.")
    fun reRequestSuccess() {
        // given
        val sender = generateUser()
        val receiver = generateUser()
        val friend = Friend(sender, receiver)

        // when
        ReflectionTestUtils.setField(friend,"status",FriendStatus.REJECTED)
        friend.reRequest()

        // then
        assertThat(friend.status).isEqualTo(FriendStatus.PENDING)
    }

    @Test
    @DisplayName("Rejected가 아닌 요청은 재요청시 예외를 반환한다.")
    fun reRequestFailed() {
        // given
        val sender = generateUser()
        val receiver = generateUser()
        val friend = Friend(sender, receiver)

        // expected
        assertThatThrownBy { friend.reRequest() }
            .isInstanceOf(RuntimeException::class.java)

        ReflectionTestUtils.setField(friend,"status",FriendStatus.APPROVED)
        assertThatThrownBy { friend.reRequest() }
            .isInstanceOf(RuntimeException::class.java)
    }

    @Test
    @DisplayName("Pending 요청을 승인시 Approved 상태로 변경된다.")
    fun approve() {
        // given
        val sender = generateUser()
        val receiver = generateUser()
        val friend = Friend(sender, receiver)

        // when
        friend.approve()

        // then
        assertThat(friend.status).isEqualTo(FriendStatus.APPROVED)
    }
}