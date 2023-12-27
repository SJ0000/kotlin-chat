package sj.chat.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint

@Configuration
@EnableWebSecurity
class SecurityConfig(

) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { csrf -> csrf.disable() }
        http.headers { headers -> headers.frameOptions { frameOptions -> frameOptions.disable() } }
        http.formLogin { formLogin -> formLogin.disable() }

        // RFC 7235에 정의된 WWW-Authenticate 헤더를 이용한 인증을 사용하지 않는다.
        http.httpBasic { httpBasic -> httpBasic.disable() }

        return http.build()
    }
}