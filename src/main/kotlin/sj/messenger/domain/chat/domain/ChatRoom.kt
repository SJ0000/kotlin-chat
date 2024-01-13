package sj.messenger.domain.chat.domain

import jakarta.persistence.Entity
import sj.messenger.global.domain.BaseEntity

@Entity
class ChatRoom(
    val name: String,
    val maxCapacity : Int
    ) : BaseEntity() {

}