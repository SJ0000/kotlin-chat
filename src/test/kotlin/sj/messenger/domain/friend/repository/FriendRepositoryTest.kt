package sj.messenger.domain.friend.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import sj.messenger.util.repository.annotation.JpaRepositoryTest
import sj.messenger.domain.friend.domain.Friend
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.util.generateUser

@JpaRepositoryTest
class FriendRepositoryTest(
    @Autowired val userRepository: UserRepository,
    @Autowired val friendRepository: FriendRepository,
) {

    @Test
    @DisplayName("특정 사용자의 모든 친구를 조회한다.")
    fun findAllTest() {
        // given
        val user = generateUser()
        val friends = (1..5).map { generateUser() }
        userRepository.save(user)
        userRepository.saveAll(friends)
        friendRepository.saveAll(friends.map { Friend(user, it) })
        // when
        val findFriends = friendRepository.findAll(user.id!!)

        // then
        assertThat(findFriends.size).isEqualTo(friends.size)
    }

    @Test
    @DisplayName("인자로 전달된 두 사용자가 친구일 경우 exists는 true를 반환한다.")
    fun existsTest() {
        // given
        val user = generateUser()
        val friends = (1..5).map { generateUser() }
        userRepository.save(user)
        userRepository.saveAll(friends)
        friendRepository.saveAll(friends.map { Friend(user, it) })

        // when
        val exists = friendRepository.exists(user.id!!, friends[0].id!!)

        // then
        assertThat(exists).isTrue()
    }

    @Test
    @DisplayName("인자로 전달된 두 사용자가 친구가 아닐 경우 exists는 false를 반환한다.")
    fun notExistsTest() {
        // given

        // when
        val exists = friendRepository.exists(1L, 2L)

        // then
        assertThat(exists).isFalse()
    }
}