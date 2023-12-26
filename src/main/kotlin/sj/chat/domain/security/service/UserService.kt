package sj.chat.domain.security.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sj.chat.domain.security.domain.User
import sj.chat.domain.security.dto.SignUp
import sj.chat.domain.security.repository.UserRepository

@Service
class UserService (
    val userRepository: UserRepository,
){
    fun findUser(id : Long) : User{
        return userRepository.findByIdOrNull(id) ?: throw RuntimeException("id $id not found")
    }

    @Transactional
    fun signUpUser(signUp : SignUp) : Long{
        // TODO : Password Encoding, 중복 email check
        val user = User(name = signUp.name, email = signUp.email, password = signUp.password)
        userRepository.save(user)
        return user.id!!
    }
}