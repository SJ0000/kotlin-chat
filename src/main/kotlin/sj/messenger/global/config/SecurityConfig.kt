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

@Configuration
@EnableWebSecurity
class SecurityConfig {

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
                apply {
                    CustomJwtConfigurer()
                }
            }
        }

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder();
    }
}

class CustomJwtConfigurer : AbstractHttpConfigurer<CustomJwtConfigurer, HttpSecurity>() {
    override fun configure(builder: HttpSecurity?) {
        // jwt 로그인, 인증,인가 필터 등록


    }
}