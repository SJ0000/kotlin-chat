package sj.messenger.domain.user.domain

import jakarta.persistence.*
import sj.messenger.global.domain.BaseEntity
import sj.messenger.global.domain.NullToEmptyStringConverter
import sj.messenger.global.domain.StringLowercaseConverter

@Entity
@Table(name = "USERS")
class User(
    var name: String,
    @Convert(converter = StringLowercaseConverter::class)
    val email: String,
    var password: String,
    var avatarUrl: String = "https://www.gravatar.com/avatar/3b3be63a4c2a439b013787725dfce802?d=identicon",

    @Convert(converter = NullToEmptyStringConverter::class)
    var statusMessage: String = "",

    var publicIdentifier: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long? = null
): BaseEntity(){

}