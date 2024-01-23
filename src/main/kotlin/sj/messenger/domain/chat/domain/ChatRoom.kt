package sj.messenger.domain.chat.domain

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import sj.messenger.domain.user.domain.User
import sj.messenger.global.domain.BaseEntity

@Entity
class ChatRoom(
    @OneToMany(mappedBy = "chatRoom", cascade = [CascadeType.PERSIST])
    val participants: MutableList<Participant> = mutableListOf(),
    val name: String,
    val avatarUrl: String = "",
) : BaseEntity() {

    fun join(user: User){
        val participant = Participant(user, this)
        participants.add(participant)
    }
}