package sj.messenger.domain.friend.repository

import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import sj.messenger.domain.friend.domain.FriendRequest
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.util.assertEntityLoaded
import sj.messenger.util.generateUser
import sj.messenger.util.repository.JpaRepositoryTest


@JpaRepositoryTest
class FriendRequestRepositoryTest(
    @Autowired val friendRequestRepository: FriendRequestRepository,
    @Autowired val userRepository: UserRepository,
    @Autowired val em : EntityManager
) {

    @BeforeEach
    fun clearTestData(){
        friendRequestRepository.deleteAll()
    }

    @Test
    @DisplayName("특정 사용자가 다른 사용자에게 요청한 친구 요청 데이터를 조회한다.")
    fun findByFromTo() {
        // given
        val sender = generateUser()
        val receiver = generateUser()
        userRepository.saveAll(listOf(sender, receiver))
        friendRequestRepository.save(FriendRequest(sender, receiver))

        // when
        val request = friendRequestRepository.findByFromTo(sender.id!!, receiver.id!!)

        // then
        assertThat(request?.sender?.id).isEqualTo(sender.id)
        assertThat(request?.receiver?.id).isEqualTo(receiver.id)
    }

    @Test
    @DisplayName("findByFromTo 메서드는 친구 요청이 존재하지 않는 경우 null 반환한다.")
    fun findByFromToNull() {
        // given
        val sender = generateUser()
        val receiver = generateUser()
        userRepository.saveAll(listOf(sender, receiver))
        friendRequestRepository.save(FriendRequest(sender, receiver))

        // when
        val request = friendRequestRepository.findByFromTo(receiver.id!!, sender.id!!)

        // then
        assertThat(request).isNull()
    }

    @Test
    @DisplayName("특정 사용자가 받은 친구 요청을 조회시 sender가 같이 조회되어야 한다.")
    fun findReceivedAllWithSender() {
        // given
        val senders = (1..4).map { generateUser() }
        val receiver = generateUser()
        userRepository.saveAll(senders)
        userRepository.save(receiver)
        friendRequestRepository.saveAll(senders.map { FriendRequest(it, receiver) })

        em.flush()
        em.clear()

        // when
        val requests = friendRequestRepository.findReceivedAllWithSender(receiver.id!!)

        // then
        assertThat(requests.size).isEqualTo(senders.size)
        assertEntityLoaded(em, *requests.map { it.sender }.toTypedArray())
    }

    @Test
    @DisplayName("친구 요청을 조회시 sender와 receiver를 같이 조회한다.")
    fun findByIdWithUser() {
        // given
        val sender = generateUser()
        val receiver = generateUser()
        userRepository.saveAll(listOf(sender, receiver))
        val request = friendRequestRepository.save(FriendRequest(sender, receiver))

        em.flush()
        em.clear()

        // when
        val result = friendRequestRepository.findByIdWithUsers(request.id!!)

        // then
        result!!
        assertEntityLoaded(em, result.sender, result.receiver)
    }
}