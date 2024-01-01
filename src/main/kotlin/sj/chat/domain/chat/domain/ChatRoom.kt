package sj.chat.domain.chat.domain

import jakarta.persistence.Entity
import sj.chat.global.domain.BaseEntity

@Entity
class ChatRoom(
    val name: String,
    val maxCapacity : Int
    ) : BaseEntity() {

}