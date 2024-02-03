package sj.messenger.domain.user.repository

import org.springframework.data.jpa.repository.JpaRepository
import sj.messenger.domain.user.domain.User


interface UserRepository : JpaRepository<User,Long?> {
    fun existsByEmail(email : String) : Boolean;
    fun findByEmail(email : String) : User?
}