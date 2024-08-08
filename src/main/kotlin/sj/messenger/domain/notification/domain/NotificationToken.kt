package sj.messenger.domain.notification.domain

import jakarta.persistence.*
import sj.messenger.domain.user.domain.User
import sj.messenger.global.domain.BaseEntity
import java.time.LocalDateTime

@Entity
class NotificationToken (
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val user: User,
    var fcmToken : String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long? = null
) : BaseEntity(){

    fun isModifiedAfter(days: Long) : Boolean{
        val expirationDay = modifiedAt.plusDays(days)
        return LocalDateTime.now().isAfter(expirationDay)
    }
}