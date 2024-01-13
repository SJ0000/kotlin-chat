package sj.messenger.domain.user.domain

import jakarta.persistence.*
import sj.messenger.global.domain.BaseEntity

@Entity
@Table(name = "USERS")
class User(
    var name: String,
    var email: String,
    var password: String,
) : BaseEntity() {


}