package sj.messenger.domain.chat.domain

import com.navercorp.fixturemonkey.kotlin.giveMeOne
import net.jqwik.api.Arbitraries
import net.jqwik.web.api.Web
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import sj.messenger.domain.user.domain.User
import sj.messenger.util.fixture
import sj.messenger.util.generateChatRoom
import sj.messenger.util.generateUser


class ChatRoomTest(
){

    @Test
    @DisplayName("join 호출시 participant를 생성하고 chatRoom의 participants에 추가된다.")
    fun joinTest(){
        // given
        val chatRoom = generateChatRoom()
        val user = generateUser()

        // when
        chatRoom.join(user)

        // then
        assertThat(chatRoom.participants.size).isEqualTo(1)
        assertThat(chatRoom.participants[0].user).isEqualTo(user)
    }

    @Test
    @DisplayName("특정 사용자가 특정 대화방의 참여자인지 여부를 확인")
    fun isParticipantTest(){
        // given
        val chatRoom = generateChatRoom()
        val user = User(
            name = fixture.giveMeOne(),
            email = Web.emails().sample(),
            password = fixture.giveMeOne(),
            id = Arbitraries.longs().sample(),
            publicIdentifier = fixture.giveMeOne()
        )

        // then
        assertThat(chatRoom.isParticipant(user.id!!)).isFalse()
    }
}