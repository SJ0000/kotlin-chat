package sj.messenger.domain.groupchat.domain

/*
    ordinal 비교를 위해 높은 권한이 먼저 정의되어야 함.
 */
enum class GroupChatRole {
    ADMIN,
    MODERATOR,
    MEMBER;

    fun canEditTarget(target: GroupChatRole): Boolean{
        if(this == MEMBER)
            return false

        return this.ordinal < target.ordinal
    }
}