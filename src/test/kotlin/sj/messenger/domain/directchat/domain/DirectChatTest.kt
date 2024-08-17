package sj.messenger.domain.directchat.domain

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import sj.messenger.util.generateUser

class DirectChatTest {

    @Test
    fun getOtherUserTest() {
        // given
        val user1 = generateUser(1L)
        val user2 = generateUser(2L)
        val directChat = DirectChat(user1, user2)

        // expected
        assertThat(directChat.getOtherUser(user1.id!!)).isEqualTo(user2)
        assertThat(directChat.getOtherUser(user2.id!!)).isEqualTo(user1)
    }

    @Test
    fun getOtherUserErrorTest() {
        // given
        val user = generateUser(1L)
        val directChat = DirectChat(generateUser(2L), generateUser(3L))

        // expected
        assertThatThrownBy { directChat.getOtherUser(user.id!!) }
            .isInstanceOf(RuntimeException::class.java)
    }

    @Test
    fun getUserTest() {
        // given
        val user1 = generateUser(1L)
        val user2 = generateUser(2L)
        val directChat = DirectChat(user1, user2)

        // expected
        assertThat(directChat.getUser(user1.id!!)).isEqualTo(user1)
        assertThat(directChat.getUser(user2.id!!)).isEqualTo(user2)
    }

    @Test
    fun getUserErrorTest() {
        // given
        val user = generateUser(1L)
        val directChat = DirectChat(generateUser(2L), generateUser(3L))

        // expected
        assertThatThrownBy { directChat.getUser(user.id!!) }
            .isInstanceOf(RuntimeException::class.java)
    }

    @Test
    fun hasAuthorityTest() {
        // given
        val users = (1..3).map { generateUser(it.toLong()) }
        val directChat = DirectChat(users[0], users[1])

        // expected
        assertThat(directChat.hasAuthority(users[0].id!!)).isTrue()
        assertThat(directChat.hasAuthority(users[1].id!!)).isTrue()
        assertThat(directChat.hasAuthority(users[2].id!!)).isFalse()
    }
}