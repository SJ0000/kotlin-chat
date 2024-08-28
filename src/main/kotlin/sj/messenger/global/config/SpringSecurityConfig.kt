package sj.messenger.global.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import sj.messenger.domain.security.authentication.interceptor.AuthenticationChannelInterceptor
import sj.messenger.domain.security.authentication.provider.JwtAuthenticationProvider
import sj.messenger.domain.security.filter.JwtAuthenticationFilter
import sj.messenger.domain.security.jwt.JwtParser

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
class SpringSecurityConfig(
    private val jwtParser: JwtParser,
) {

    @Bean
    @Qualifier("SecurityConfig")
    fun filterChain(http: HttpSecurity, authenticationManager: AuthenticationManager): SecurityFilterChain {
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

            addFilterBefore<UsernamePasswordAuthenticationFilter>(JwtAuthenticationFilter(authenticationManager))
        }

        return http.build()
    }

    @Bean
    fun authenticationManager(): AuthenticationManager {
        val jwtAuthenticationProvider = JwtAuthenticationProvider(jwtParser)
        return ProviderManager(jwtAuthenticationProvider);
    }

    @Bean
    fun authenticationChannelInterceptor(authenticationManager: AuthenticationManager): AuthenticationChannelInterceptor {
        return AuthenticationChannelInterceptor(authenticationManager)
    }
}