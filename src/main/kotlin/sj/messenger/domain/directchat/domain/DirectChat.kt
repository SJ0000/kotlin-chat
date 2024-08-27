package sj.messenger.domain.directchat.domain

import jakarta.persistence.*
import sj.messenger.domain.user.domain.User
import sj.messenger.global.domain.BaseEntity

@Entity
class DirectChat(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val user1: User,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val user2: User,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
) : BaseEntity() {

    fun getUsers(): List<User> {
        return listOf(user1, user2)
    }

    fun getOtherUser(myId: Long): User {
        return when (myId) {
            user1.id -> user2
            user2.id -> user1
            else -> throw IllegalArgumentException("User(id = ${myId}) is not participant")
        }
    }

    fun getUser(userId: Long): User {
        return when (userId) {
            user1.id -> user1
            user2.id -> user2
            else -> throw IllegalArgumentException("User(id = ${userId}) is not participant")
        }
    }

    fun hasAuthority(userId: Long): Boolean {
        return when (userId) {
            user1.id -> true
            user2.id -> true
            else -> false
        }
    }
}