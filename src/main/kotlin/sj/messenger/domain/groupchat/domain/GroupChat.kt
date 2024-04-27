package sj.messenger.domain.groupchat.domain

import jakarta.persistence.*
import sj.messenger.domain.user.domain.User
import sj.messenger.global.domain.BaseEntity

@Entity
class GroupChat(

    val name: String,
    val avatarUrl: String = "https://www.gravatar.com/avatar/3b3be63a4c2a439b013787725dfce802?d=identicon",

    @OneToMany(mappedBy = "groupChat", cascade = [CascadeType.PERSIST])
    val participants: MutableList<Participant> = mutableListOf(),

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long? = null
): BaseEntity() {

    fun join(user: User){
        val participant = Participant(user, this)
        participants.add(participant)
    }

    fun isParticipant(userId : Long) : Boolean{
        return participants.any {
            it.user.id == userId
        }
    }

    fun getParticipantUserIds() : Set<Long>{
        return participants.map { it.user.id!! }.toSet()
    }
}