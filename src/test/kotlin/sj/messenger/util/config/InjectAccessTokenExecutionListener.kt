package sj.messenger.util.config

import jakarta.servlet.FilterChain
import jakarta.servlet.GenericFilter
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import org.springframework.security.web.SecurityFilterChain
import org.springframework.test.context.TestContext
import org.springframework.test.context.TestExecutionListener
import sj.messenger.domain.security.jwt.JwtProvider
import sj.messenger.domain.security.jwt.UserClaim
import sj.messenger.domain.user.dto.SignUpDto
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.domain.user.service.UserService
import java.lang.reflect.Method

/*
mockUser
name = test
email = test@test.com
password = 1234567890
*/

class InjectAccessTokenExecutionListener : TestExecutionListener {

    private lateinit var jwtProvider: JwtProvider
    private lateinit var securityFilterChain: SecurityFilterChain
    private lateinit var userService: UserService
    private lateinit var userRepository: UserRepository

    companion object {
        private const val TEST_USER_EMAIL = "test@test.com"
    }

    override fun beforeTestMethod(testContext: TestContext) {
        initialize(testContext)
        clearContext()

        if (isTarget(testContext.testMethod)) {
            createTestUser()
            mockAuthorizationHeader()
        }
    }

    private fun initialize(context: TestContext) {
        jwtProvider = context.applicationContext.getBean(JwtProvider::class.java)
        securityFilterChain = context.applicationContext.getBean(SecurityFilterChain::class.java)
        userService = context.applicationContext.getBean(UserService::class.java)
        userRepository = context.applicationContext.getBean(UserRepository::class.java)
    }

    private fun clearContext() {
        // filter 제거
        val filters = securityFilterChain.filters
        filters.removeIf { it is AccessTokenProvideFilter }
    }

    private fun isTarget(method: Method): Boolean {
        return method.isAnnotationPresent(InjectAccessToken::class.java)
    }

    private fun mockAuthorizationHeader() {
        val user = userService.findUserByEmail(TEST_USER_EMAIL)
        val accessToken = jwtProvider.createAccessToken(UserClaim(user.id!!, user.name))
        val filters = securityFilterChain.filters
        filters.add(0, AccessTokenProvideFilter(accessToken))
    }

    private fun createTestUser() {
        userService.signUpUser(
            SignUpDto(
                email = TEST_USER_EMAIL,
                name = "test",
                password = "1234567890"
            )
        )
    }
}

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class InjectAccessToken()

class AccessTokenProvideFilter(
    private val accessToken: String,
) : GenericFilter() {
    override fun doFilter(p0: ServletRequest?, p1: ServletResponse?, p2: FilterChain?) {
        val header = Pair("Authorization", "Bearer ${accessToken}")
        p2?.doFilter(HeaderAddWrapper(p0 as HttpServletRequest, header), p1)
    }

    class HeaderAddWrapper(
        request: HttpServletRequest,
        private val header: Pair<String, String>
    ) : HttpServletRequestWrapper(request) {

        override fun getHeader(name: String?): String {
            if (name == header.first) {
                return header.second
            }
            return super.getHeader(name)
        }
    }
}