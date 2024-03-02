package sj.messenger.util.repository

import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container

class RedisContainerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    companion object{
        @JvmStatic
        val redisContainer = GenericContainer<Nothing>("redis:latest").apply {
            withExposedPorts(6379)
        }
    }

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        redisContainer.start()
        TestPropertyValues.of(
            "spring.data.redis.host=${redisContainer.host}",
            "spring.data.redis.port=${redisContainer.getMappedPort(6379)}",
        ).applyTo(applicationContext.environment)
    }
}