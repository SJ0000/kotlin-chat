package sj.messenger.global.config

import org.hibernate.validator.constraints.Length
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.validation.annotation.Validated

@Configuration
@EnableConfigurationProperties(RedisConfigurationProperties::class)
class RedisConfig(
    private val properties: RedisConfigurationProperties
) {

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        return LettuceConnectionFactory(properties.host, properties.port)
    }

}

@Validated
@ConfigurationProperties("spring.data.redis")
data class RedisConfigurationProperties(
    val host: String,
    val port: Int,
)
