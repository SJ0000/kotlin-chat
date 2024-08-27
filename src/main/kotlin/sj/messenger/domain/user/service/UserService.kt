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
import sj.messenger.global.exception.AlreadyExistsException
import sj.messenger.global.exception.EntityNotFoundException

@Service
@Transactional(readOnly = true)
class UserService(
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder,
) {

    fun findUserById(id: Long): User {
        return userRepository.findByIdOrNull(id) ?: throw EntityNotFoundException(User::class, "id", id)
    }

    fun findUserByEmail(email: String): User {
        return userRepository.findByEmail(email) ?: throw EntityNotFoundException(User::class, "email", email)
    }

    fun findUserByPublicIdentifier(publicIdentifier: String): User {
        return userRepository.findByPublicIdentifier(publicIdentifier)
            ?: throw EntityNotFoundException(User::class, "publicIdentifier", publicIdentifier)
    }

    fun findUsers(ids: Set<Long>): List<User> {
        val users = userRepository.findAllById(ids)
        if (users.size != ids.size) {
            val notExists = ids.subtract(users.map { it.id!! }.toSet())
            throw EntityNotFoundException(User::class, "id list", notExists)
        }
        return users
    }

    @Transactional
    fun signUpUser(signUp: SignUpDto): Long {
        val encodedPassword = passwordEncoder.encode(signUp.password)

        if (existsEmail(signUp.email))
            throw AlreadyExistsException(User::class, "email", signUp.email)

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
        val user = findUserById(id)
        if (user.publicIdentifier != updateUser.publicIdentifier)
            validateUniquePublicIdentifier(updateUser.publicIdentifier)

        with(user) {
            name = updateUser.name
            avatarUrl = updateUser.avatarUrl
            statusMessage = updateUser.statusMessage
            publicIdentifier = updateUser.publicIdentifier
        }
    }

    fun existsEmail(email: String): Boolean {
        return userRepository.existsByEmail(email);
    }

    private fun existsPublicIdentifier(identifier: String): Boolean {
        return userRepository.existsByPublicIdentifier(identifier);
    }

    private fun validateUniquePublicIdentifier(identifier: String) {
        if (existsPublicIdentifier(identifier))
            throw AlreadyExistsException(User::class, "publicIdentifier", identifier)
    }

    private fun createPublicIdentifier(userName: String): String {
        // 3번 시도 후 자릿수 늘리기
        (5..8).forEach { length ->
            (1..3).forEach { _ ->
                val created = "${userName}#${generateRandomNumbers(length)}"
                if (!existsPublicIdentifier(created))
                    return created
            }
        }
        throw RuntimeException("Public Identifier 생성에 실패하였습니다.")
    }

    private fun generateRandomNumbers(length: Int): String {
        return (1..length).map { ('0'..'9').toList().random() }.joinToString("")
    }
}