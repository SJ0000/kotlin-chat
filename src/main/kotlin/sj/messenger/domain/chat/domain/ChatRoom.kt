package sj.messenger.domain.chat.domain

import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import sj.messenger.global.domain.BaseEntity

@Entity
class ChatRoom(
    @OneToMany(mappedBy = "chatRoom")
    val participants: MutableList<Participant> = mutableListOf()
    ) : BaseEntity() {

}