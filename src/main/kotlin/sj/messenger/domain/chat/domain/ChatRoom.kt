package sj.messenger.domain.chat.domain

import jakarta.persistence.*
import sj.messenger.domain.user.domain.User
import sj.messenger.global.domain.BaseEntity

@Entity
class ChatRoom(
    val name: String,
    val avatarUrl: String = "",

    @OneToMany(mappedBy = "chatRoom", cascade = [CascadeType.PERSIST])
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
}