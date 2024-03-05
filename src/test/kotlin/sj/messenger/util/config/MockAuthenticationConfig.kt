package sj.messenger.util.config

import jakarta.annotation.PostConstruct
import jakarta.servlet.FilterChain
import jakarta.servlet.GenericFilter
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.security.web.SecurityFilterChain
import sj.messenger.domain.security.jwt.JwtProvider
import sj.messenger.domain.security.jwt.UserClaim
import sj.messenger.domain.user.dto.SignUpDto
import sj.messenger.domain.user.service.UserService

lateinit var currentSecurityFilterChain: SecurityFilterChain
lateinit var currentJwtProvider: JwtProvider
lateinit var currentUserService: UserService

@TestConfiguration
class MockAuthenticationConfig(
    @Qualifier("SecurityConfig") private val securityFilterChain: SecurityFilterChain,
    private val jwtProvider: JwtProvider,
    private val userService: UserService,
) {
    @PostConstruct
    fun initializeLateInitBeans(){
        currentSecurityFilterChain = securityFilterChain
        currentJwtProvider = jwtProvider
        currentUserService = userService
    }
}

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class WithMockAccessToken()

/*
    mockUser
    name = test
    email = test@test.com
    password = 1234567890
 */
class AccessTokenProvideExtension : BeforeTestExecutionCallback{

    override fun beforeTestExecution(context: ExtensionContext?) {
        val method = context?.testMethod?.get()!!
        if(method.isAnnotationPresent(WithMockAccessToken::class.java)){
            mockAuthorizationHeader()
        }
    }

    private fun mockAuthorizationHeader(){
        val signUp = SignUpDto(
            email = "test@test.com",
            name = "test",
            password = "1234567890"
        )
        val userId = currentUserService.signUpUser(signUp)
        val accessToken = currentJwtProvider.createAccessToken(UserClaim(userId, signUp.name))
        addAccessTokenProvideFilter(accessToken)
    }

    private fun addAccessTokenProvideFilter(accessToken: String){
        val filters = currentSecurityFilterChain.filters
        filters.add(0,AccessTokenProvideFilter(accessToken))
    }
}

class AccessTokenProvideFilter(
    private val accessToken: String,
) : GenericFilter() {
    override fun doFilter(p0: ServletRequest?, p1: ServletResponse?, p2: FilterChain?) {
        val header = Pair("Authorization", "Bearer ${accessToken}")
        p2?.doFilter(HeaderAddWrapper(p0 as HttpServletRequest, header), p1)
    }
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