package sj.chat.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig(

) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.formLogin { formLogin -> formLogin.disable() }
        return http.build()
    }
}