package sj.messenger.domain.user.domain

import jakarta.persistence.Entity
import jakarta.persistence.Table
import sj.messenger.global.domain.BaseEntity

@Entity
@Table(name = "USERS")
class User(
    var name: String,
    var email: String,
    var password: String,
    var avatarUrl: String = ""
) : BaseEntity() {

}