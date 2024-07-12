package sj.messenger.domain.friend.service

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import sj.messenger.domain.friend.domain.Friend
import sj.messenger.domain.friend.domain.FriendRequest
import sj.messenger.domain.friend.repository.FriendRepository
import sj.messenger.domain.friend.repository.FriendRequestRepository
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.util.generateUser
import sj.messenger.util.integration.EnableContainers

@SpringBootTest
@EnableContainers
@Transactional
class FriendServiceTest(
    @Autowired val friendService: FriendService,
    @Autowired val userRepository: UserRepository,
    @Autowired val friendRepository: FriendRepository,
    @Autowired val friendRequestRepository: FriendRequestRepository,
) {
    @Test
    @DisplayName("특정 사용자의 친구 조회")
    fun getFriends() {
        // given
        val user = generateUser(10L)
        val friends = (1L..5L).map { generateUser() }
        userRepository.saveAll(listOf(user) + friends)
        friendRepository.saveAll(friends.map { Friend(user, it) })

        // when
        val findFriends = friendService.getFriends(user.id!!)

        // then
        assertThat(findFriends.size).isEqualTo(friends.size)
        assertThat(findFriends.map { it.id!! })
            .containsAll(findFriends.map { it.id!! })
    }

    @Test
    @DisplayName("친구 요청 성공")
    fun request() {
        // given
        val sender = generateUser(1L)
        val receiver = generateUser(2L)
        userRepository.saveAll(listOf(sender, receiver))

        // when
        friendService.request(sender.id!!, receiver.publicIdentifier)

        // then
        val request = friendRequestRepository.findByFromTo(sender.id!!, receiver.id!!)
        assertThat(request).isNotNull
    }

    @Test
    @DisplayName("이미 친구인 경우 친구 요청시 예외 발생")
    fun requestAlreadyFriendsError() {
        // given
        val sender = generateUser()
        val receiver = generateUser()
        userRepository.saveAll(listOf(sender, receiver))
        friendRepository.save(Friend(sender,receiver))

        // expected
        assertThatThrownBy { friendService.request(sender.id!!, receiver.publicIdentifier) }
            .isInstanceOf(RuntimeException::class.java)
    }

    @Test
    @DisplayName("같은 사용자를 대상으로 보낸 친구 요청이 이미 존재할 경우 예외 발생")
    fun requestHasRequestError() {
        // given
        val sender = generateUser()
        val receiver = generateUser()
        userRepository.saveAll(listOf(sender, receiver))
        friendRequestRepository.save(FriendRequest(sender,receiver))

        // expected
        assertThatThrownBy { friendService.request(sender.id!!, receiver.publicIdentifier) }
            .isInstanceOf(RuntimeException::class.java)
    }

    @Test
    @DisplayName("친구 요청 정상 수락")
    fun approveRequest() {
        // given
        val sender = generateUser()
        val receiver = generateUser()
        userRepository.saveAll(listOf(sender, receiver))
        val request = friendRequestRepository.save(FriendRequest(sender, receiver))

        // when
        friendService.approveRequest(receiver.id!!, request.id!!)

        // then
        val exists = friendRepository.exists(sender.id!!, receiver.id!!)
        assertThat(exists).isTrue()
    }

    @Test
    @DisplayName("자신이 받은 친구 요청이 아닐 경우 수락시 예외 발생")
    fun approveRequestError() {
        // given
        val sender = generateUser()
        val receiver = generateUser()
        val other = generateUser()
        userRepository.saveAll(listOf(sender, receiver, other))
        val request = friendRequestRepository.save(FriendRequest(sender, receiver))

        // expected
        assertThatThrownBy { friendService.approveRequest(other.id!!, request.id!!) }
            .isInstanceOf(RuntimeException::class.java)
    }
}