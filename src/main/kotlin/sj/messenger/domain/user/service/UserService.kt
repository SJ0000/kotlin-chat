package sj.messenger.domain.user.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sj.messenger.domain.security.dto.LoginRequest
import sj.messenger.domain.user.domain.User
import sj.messenger.domain.user.dto.SignUpDto
import sj.messenger.domain.user.repository.UserRepository

@Service
class UserService (
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder,
){
    @Transactional
    fun findUser(id : Long) : User {
        return userRepository.findByIdOrNull(id) ?: throw RuntimeException("user not found. id = ${id}")
    }

    @Transactional
    fun findUser(email : String) : User {
        return userRepository.findByEmail(email) ?: throw RuntimeException("user not found. email =  ${email}")
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

    @Transactional
    fun validateLogin(loginRequest: LoginRequest){
        val user = findUser(loginRequest.email)
        if(!passwordEncoder.matches(loginRequest.password,user.password))
            throw RuntimeException("login failed - incorrect password. email = ${loginRequest.email}")
    }

    private fun existsEmail(email: String) : Boolean{
        return userRepository.existsByEmail(email);
    }
}