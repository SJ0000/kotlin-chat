package sj.messenger.domain.user.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sj.messenger.domain.security.dto.LoginRequest
import sj.messenger.domain.user.domain.User
import sj.messenger.domain.user.dto.SignUpDto
import sj.messenger.domain.user.dto.UpdateUserDto
import sj.messenger.domain.user.repository.UserRepository

@Service
@Transactional(readOnly = true)
class UserService(
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder,
) {

    fun findUserById(id: Long): User {
        return userRepository.findByIdOrNull(id) ?: throw RuntimeException("user not found. id = ${id}")
    }

    fun findUserByEmail(email: String): User {
        return userRepository.findByEmail(email) ?: throw RuntimeException("user not found. email =  ${email}")
    }

    fun findUserByPublicIdentifier(publicIdentifier: String) : User{
        return userRepository.findByPublicIdentifier(publicIdentifier) ?: throw RuntimeException("user not found. publicIdentifier =  ${publicIdentifier}")
    }

    fun findUsers(ids : List<Long>) : List<User>{
        return userRepository.findAllById(ids)
    }

    @Transactional
    fun signUpUser(signUp: SignUpDto): Long {
        val encodedPassword = passwordEncoder.encode(signUp.password)

        if (existsEmail(signUp.email))
            throw RuntimeException("email ${signUp.email} already exists")

        val publicIdentifier = createPublicIdentifier(signUp.name)

        val user = User(
            name = signUp.name,
            email = signUp.email,
            password = encodedPassword,
            publicIdentifier = publicIdentifier
        )
        userRepository.save(user)
        return user.id!!
    }


    fun validateLogin(loginRequest: LoginRequest) {
        val user = findUserByEmail(loginRequest.email)
        if (!passwordEncoder.matches(loginRequest.password, user.password))
            throw RuntimeException("login failed - incorrect password. email = ${loginRequest.email}")
    }

    @Transactional
    fun updateUser(id: Long, updateUser: UpdateUserDto) {
        with(findUserById(id)) {
            name = updateUser.name
            avatarUrl = updateUser.avatarUrl
            statusMessage = updateUser.statusMessage
            publicIdentifier = updateUser.publicIdentifier
        }
    }

    private fun existsEmail(email: String): Boolean {
        return userRepository.existsByEmail(email);
    }

    private fun existsPublicIdentifier(identifier: String): Boolean {
        return userRepository.existsByPublicIdentifier(identifier);
    }

    private fun createPublicIdentifier(userName: String): String {
        var created: String
        do {
            created = "${userName}#${generateRandomNumbers()}"
        } while (existsPublicIdentifier(created))
        return created
    }

    private fun generateRandomNumbers(): String {
        return (1..5).map { ('0'..'9').toList().random() }.joinToString("")
    }
}