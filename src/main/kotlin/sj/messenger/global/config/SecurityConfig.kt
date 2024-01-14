package sj.messenger.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import sj.messenger.domain.security.filter.JwtProvideLoginFilter
import sj.messenger.domain.security.jwt.JwtParser
import sj.messenger.domain.security.jwt.JwtProvider
import sj.messenger.domain.user.service.UserService

@Configuration
@EnableWebSecurity(debug = true)
class SecurityConfig (
    private val jwtProvider: JwtProvider,
    private val jwtParser: JwtParser,
    private val userService: UserService
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }
            cors { disable() }
            headers {
                frameOptions {
                    disable()
                }
            }
            formLogin { disable() }
            httpBasic { disable() } // RFC 7235 WWW-Authenticate 인증 미사용
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }

            addFilterBefore<UsernamePasswordAuthenticationFilter>(JwtProvideLoginFilter(jwtProvider,userService))
        }

        return http.build()
    }
}