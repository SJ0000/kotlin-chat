package sj.messenger.domain.groupchat.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import sj.messenger.util.generateGroupChat
import sj.messenger.util.generateUser


class GroupChatTest(
){

    @Test
    @DisplayName("join 호출시 participant를 생성하고 chatRoom의 participants에 추가된다.")
    fun joinTest(){
        // given
        val chatRoom = generateGroupChat(generateUser())
        val user = generateUser()

        // when
        chatRoom.join(user)

        // then
        assertThat(chatRoom.participants.size).isEqualTo(2)
    }

    @Test
    @DisplayName("특정 사용자가 특정 대화방의 참여자인지 여부를 확인")
    fun isParticipantTest(){
        // given
        val user = generateUser(1L)
        val chatRoom = generateGroupChat(user)

        // then
        assertThat(chatRoom.isParticipant(user.id!!)).isTrue()
    }

}