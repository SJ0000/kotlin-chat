package sj.messenger.domain.directchat.domain

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import sj.messenger.util.generateUser

class DirectChatTest{

    @Test
    fun getOtherUserTest(){
        // given
        val user1 = generateUser()
        val user2 = generateUser()
        val directChat = DirectChat(user1, user2)

        // when
        val otherUser = directChat.getOtherUser(user1.id!!)

        // then
        assertThat(otherUser).isEqualTo(user2)
    }

    @Test
    fun getOtherUserErrorTest(){
        // given
        val user = generateUser()
        val directChat = DirectChat(generateUser(), generateUser())

        // expected
        assertThatThrownBy { directChat.getOtherUser(user.id!!) }
            .isInstanceOf(RuntimeException::class.java)
    }
}