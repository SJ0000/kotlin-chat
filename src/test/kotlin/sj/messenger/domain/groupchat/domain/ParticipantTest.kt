package sj.messenger.domain.groupchat.domain

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import sj.messenger.util.generateUser
import sj.messenger.util.randomString

class ParticipantTest(){

    @Test
    @DisplayName("자신보다 높은 권한을 가진 참여자는 자신의 정보를 수정할 수 있다.")
    fun canModifyTest(){
        // given
        val admin = generateUser(1L)
        val member = generateUser(2L)
        val groupChat = GroupChat.create(admin, randomString(10))
        groupChat.join(member)

        // when
        val adminParticipant = groupChat.getParticipant(admin.id!!)!!
        val memberParticipant = groupChat.getParticipant(member.id!!)!!

        // then
        assertThat(adminParticipant.canModify(memberParticipant)).isTrue()
        assertThat(memberParticipant.canModify(adminParticipant)).isFalse()
    }

    @Test
    @DisplayName("다른 대화방 참여자의 정보는 수정 할 수 없다.")
    fun canModifyFailedTest(){
        // given
        val admin = generateUser(1L)
        val otherAdmin = generateUser(2L)
        val groupChat = GroupChat.create(admin, randomString(10))
        val otherGroupChat = GroupChat.create(otherAdmin, randomString(10))

        // when
        val participant = groupChat.getParticipant(admin.id!!)!!
        val otherParticipant = otherGroupChat.getParticipant(otherAdmin.id!!)!!

        // then
        assertThat(participant.canModify(otherParticipant)).isFalse()
    }
}