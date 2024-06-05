package sj.messenger.domain.groupchat.domain

import jakarta.persistence.*
import sj.messenger.domain.user.domain.User
import sj.messenger.global.domain.BaseEntity

@Entity
class GroupChat private constructor(
    val name: String,
    val avatarUrl: String = "https://www.gravatar.com/avatar/3b3be63a4c2a439b013787725dfce802?d=identicon",

    @OneToMany(mappedBy = "groupChat", cascade = [CascadeType.PERSIST])
    val participants: MutableList<Participant> = mutableListOf(),

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long? = null
): BaseEntity() {

    companion object{
        @JvmStatic
        fun create(creator: User, name: String) : GroupChat{
            val groupChat = GroupChat(name);
            groupChat.assignAdmin(creator)
            return groupChat;
        }
    }

    private fun assignAdmin(user: User){
        val admin = Participant(user, this, GroupChatRole.ADMIN)
        participants.add(admin)
    }

    fun join(user: User){
        val participant = Participant(user, this, GroupChatRole.MEMBER)
        participants.add(participant)
    }

    fun isParticipant(userId : Long) : Boolean{
        return participants.any {
            it.user.id == userId
        }
    }

    fun getParticipant(userId: Long): Participant?{
        return participants.find { it.user.id == userId }
    }

    fun getParticipantUserIds() : Set<Long>{
        return participants.map { it.user.id!! }.toSet()
    }
}