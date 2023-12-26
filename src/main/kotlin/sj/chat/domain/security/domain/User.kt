package sj.chat.domain.security.domain

import jakarta.persistence.*
import sj.chat.global.domain.BaseEntity

@Entity
@Table(name = "USERS")
class User (
    var name : String,
    var email : String,
    var password : String,
) : BaseEntity() {




}