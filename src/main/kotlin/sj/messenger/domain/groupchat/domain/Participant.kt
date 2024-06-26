package sj.messenger.domain.groupchat.domain

import jakarta.persistence.*
import sj.messenger.domain.user.domain.User
import sj.messenger.global.domain.BaseEntity

@Entity
class Participant(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_chat_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val groupChat: GroupChat,

    @Enumerated(EnumType.STRING)
    var role: GroupChatRole,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long? = null
) : BaseEntity(){

    fun canModify(target: Participant): Boolean {
        if(this.groupChat.id != target.groupChat.id)
            return false;

        return this.role.canEditTarget(target.role)
    }
}