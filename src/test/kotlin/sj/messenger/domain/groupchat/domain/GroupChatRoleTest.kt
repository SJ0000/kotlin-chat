package sj.messenger.domain.groupchat.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class GroupChatRoleTest() {

    @Test
    @DisplayName("ADMIN과 MODERATOR는 자신보다 권한 등급이 낮은 target을 수정할 수 있다.")
    fun canEditTarget() {
        val admin = GroupChatRole.ADMIN
        val moderator = GroupChatRole.MODERATOR
        val member = GroupChatRole.MEMBER

        assertThat(admin.canEditTarget(moderator)).isTrue()
        assertThat(admin.canEditTarget(member)).isTrue()
        assertThat(moderator.canEditTarget(member)).isTrue()
    }

    @Test
    @DisplayName("같은 권한을 가진 target은 수정 할 수 없다.")
    fun canEditTargetSameRole() {
        val admin = GroupChatRole.ADMIN
        val moderator = GroupChatRole.MODERATOR

        assertThat(admin.canEditTarget(admin)).isFalse()
        assertThat(moderator.canEditTarget(moderator)).isFalse()
    }

    @Test
    @DisplayName("권한 등급이 MEMBER인 경우, 모든 target을 수정 할 수 없다.")
    fun canEditTargetMember() {
        val member = GroupChatRole.MEMBER

        GroupChatRole.entries.forEach {
            assertThat(member.canEditTarget(it)).isFalse()
        }
    }
}
