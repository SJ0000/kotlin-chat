package sj.messenger.domain.friend.domain

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import sj.messenger.util.generateUser

class FriendRequestTest {

    @Test
    @DisplayName("인자로 받은 사용자가 친구 요청을 받은 사용자인지 확인")
    fun approveIfPossible() {
        // given
        val sender = generateUser(1L)
        val receiver = generateUser(2L)
        val friendRequest = FriendRequest(sender, receiver)

        // when
        friendRequest.approveIfPossible(receiver.id!!)

        // then
        assertThat(friendRequest.approved).isTrue()
    }

    @Test
    @DisplayName("Receiver가 아닌 경우 승인 시도시 IllegalArgumentException 반환")
    fun approveIfPossibleNotReceiver() {
        // given
        val sender = generateUser(1L)
        val receiver = generateUser(2L)
        val request = FriendRequest(sender, receiver)

        // expected
        assertThatThrownBy {
            request.approveIfPossible(receiver.id!!+1)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    @DisplayName("이미 승인된 요청을 승인 시도시 IllegalStateException 반환")
    fun approveIfPossibleAlreadyApproved() {
        // given
        val sender = generateUser(1L)
        val receiver = generateUser(2L)
        val request = FriendRequest(sender, receiver)

        // expected
        request.approveIfPossible(receiver.id!!)

        assertThatThrownBy {
            request.approveIfPossible(receiver.id!!)
        }.isInstanceOf(IllegalStateException::class.java)
    }
}