package sj.messenger.domain.notification.domain

import jakarta.persistence.*
import sj.messenger.domain.user.domain.User
import sj.messenger.global.domain.BaseEntity

@Entity
class NotificationToken (
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val user: User,
    val fcmToken : String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long? = null
) : BaseEntity(){
}