package sj.messenger.domain.directchat.domain

import jakarta.persistence.*
import sj.messenger.domain.user.domain.User
import sj.messenger.global.domain.BaseEntity

@Entity
class DirectChat(
    @ManyToOne(fetch = FetchType.LAZY)
    val user1 : User,
    @ManyToOne(fetch = FetchType.LAZY)
    val user2 : User,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long? = null
) : BaseEntity() {

    fun getUsers() : List<User>{
        return listOf(user1,user2)
    }

    fun getOtherUser(myId: Long) : User{
        if(user1.id == myId)
            return user2
        if(user2.id == myId)
            return user1
        throw RuntimeException("User(id = ${myId}) is not participant")
    }

    fun hasAuthority(userId: Long) : Boolean{
        return (user1.id == userId) || (user2.id == userId)
    }
}