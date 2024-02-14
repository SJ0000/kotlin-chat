package sj.messenger.domain.user.domain

import jakarta.persistence.*
import sj.messenger.global.domain.BaseEntity

@Entity
@Table(name = "USERS")
class User(
    var name: String,
    var email: String,
    var password: String,
    var avatarUrl: String = "https://www.gravatar.com/avatar/3b3be63a4c2a439b013787725dfce802?d=identicon",
    var statusMessage: String = "",

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long? = null
){

}