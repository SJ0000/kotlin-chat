package sj.chat.domain.security.repository

import org.springframework.data.jpa.repository.JpaRepository
import sj.chat.domain.security.domain.User

interface UserRepository : JpaRepository<User,Long?> {
    fun existsByEmail(email : String) : Boolean;
}