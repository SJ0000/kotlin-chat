package sj.messenger.domain.friend.repository

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import sj.messenger.RepositoryTest
import sj.messenger.domain.friend.domain.Friend
import sj.messenger.domain.friend.domain.FriendStatus
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.util.generateUser
import sj.messenger.global.config.QueryDslConfig


@RepositoryTest
class FriendRepositoryTest(
    @Autowired private val friendRepository: FriendRepository,
    @Autowired private val userRepository: UserRepository,
) {

    @Test
    @DisplayName("특정 사용자가 받은 친구 요청을 조회한다.")
    fun findAllByToUserIdWithFromUserTest(){
        // given
        val toUser = generateUser()
        userRepository.save(toUser)
        val approved = (1..2).map { generateUser() }
        val pending = (1..4).map { generateUser() }
        userRepository.saveAll(approved)
        userRepository.saveAll(pending)

        friendRepository.saveAll(approved.map {
            val friend = Friend(it, toUser)
            friend.approve()
            friend
        })
        friendRepository.saveAll(pending.map { Friend(it,toUser) })

        // when
        val result =
            friendRepository.findAllByToUserIdWithFromUser(toUser.id!!, FriendStatus.PENDING)

        // then
        assertThat(result.size).isEqualTo(4)
        assertThat(result).allMatch {
            it.toUser == toUser
        }
    }

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
    @DisplayName("특정 사용자의 친구 목록을 조회한다.")
    fun findApprovedAllTest() {
        // given
        val user = generateUser()
        userRepository.save(user)
        val users = (1..10).map { generateUser() }
        userRepository.saveAll(users)

        friendRepository.saveAll(users.map {
            val friend = Friend(user, it)
            friend.approve()
            friend
        })

        // when
        val result = friendRepository.findApprovedAll(user.id!!)
        // then
        assertThat(result.size).isEqualTo(10)
    }
}