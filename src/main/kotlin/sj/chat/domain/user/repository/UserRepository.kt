package sj.chat.domain.user.repository

import org.springframework.data.jpa.repository.JpaRepository
import sj.chat.domain.user.domain.User

interface UserRepository : JpaRepository<User,Long?> {
    fun existsByEmail(email : String) : Boolean;
}