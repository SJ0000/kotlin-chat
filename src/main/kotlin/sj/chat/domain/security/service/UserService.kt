package sj.chat.domain.security.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sj.chat.domain.security.domain.User
import sj.chat.domain.security.dto.SignUpDto
import sj.chat.domain.security.repository.UserRepository

@Service
class UserService (
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder,
){
    fun findUser(id : Long) : User{
        return userRepository.findByIdOrNull(id) ?: throw RuntimeException("id $id not found")
    }

    @Transactional
    fun signUpUser(signUp : SignUpDto) : Long{
        // TODO : Password Encoding, 중복 email check
        val encodedPassword = passwordEncoder.encode(signUp.password)

        if(existsEmail(signUp.email))
            throw RuntimeException("email ${signUp.email} already exists")

        val user = User(name = signUp.name, email = signUp.email, password = encodedPassword)
        userRepository.save(user)
        return user.id!!
    }

    private fun existsEmail(email: String) : Boolean{
        return userRepository.existsByEmail(email);
    }
}