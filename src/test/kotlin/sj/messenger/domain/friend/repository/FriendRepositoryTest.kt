package sj.messenger.domain.friend.repository

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import sj.messenger.domain.friend.domain.Friend
import sj.messenger.domain.friend.domain.FriendStatus
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.fixture.generateUser
import sj.messenger.global.config.QueryDslConfig


@DataJpaTest
@Import(QueryDslConfig::class)
class FriendRepositoryTest(
    @Autowired private val friendRepository: FriendRepository,
    @Autowired private val userRepository: UserRepository,
) {

    @Test
    @DisplayName("주어진 파라미터와 일치하는 Friend의 존재 유무 확인")
    fun existsTest() {
        // given
        val from = generateUser()
        val to = generateUser()
        userRepository.saveAll(listOf(from, to))
        friendRepository.save(Friend(fromUser = from, toUser = to))

        // when
        val existsPending = friendRepository.exists(from.id!!, to.id!!, FriendStatus.PENDING)
        val existsAPPROVED = friendRepository.exists(from.id!!, to.id!!, FriendStatus.APPROVED)

        // then
        assertThat(existsPending).isTrue()
        assertThat(existsAPPROVED).isFalse()
    }

    @Test
    @DisplayName("from과 to가 바뀐 Friend도 조회되어야 한다.")
    fun existsIngoreFromToTest() {
        // given
        val from = generateUser()
        val to = generateUser()
        userRepository.saveAll(listOf(from, to))
        friendRepository.save(Friend(fromUser = from, toUser = to))

        // when
        val existsFromTo = friendRepository.existsIgnoreFromTo(from.id!!, to.id!!, FriendStatus.PENDING)
        val existsToFrom = friendRepository.existsIgnoreFromTo(to.id!!, from.id!!, FriendStatus.PENDING)

        // then
        assertThat(listOf(existsFromTo, existsToFrom)).allMatch { it }
    }

    @Test
    @DisplayName("주어진 파라미터와 일치하는 Friend 조회")
    fun findByFromToTest() {
        // given
        val from = generateUser()
        val to = generateUser()
        userRepository.saveAll(listOf(from, to))
        val friend = Friend(fromUser = from, toUser = to)
        friendRepository.save(friend)

        // when
        val findFriend = friendRepository.findByFromTo(from.id!!, to.id!!, FriendStatus.PENDING)

        // then
        assertThat(findFriend).isEqualTo(friend)
    }

    @Test
    @DisplayName("특정 사용자가 받은 친구 요청을 조회한다.")
    fun findAllByToUserId() {
        // given
        val toUser = generateUser()
        val users = (1..3).map { generateUser() }
        userRepository.saveAll(users)
        userRepository.save(toUser)

        friendRepository.saveAll(users.map { Friend(it, toUser) })
        // when
        val result = friendRepository.findAllByToUserId(toUser.id!!)

        // then
        assertThat(result.size).isEqualTo(3)
        assertThat(result).allMatch {
            it.toUser == toUser
        }
    }
}