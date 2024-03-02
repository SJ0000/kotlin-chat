package sj.messenger.util.testcontainer.initializer

import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.GenericContainer

class RedisContainerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    companion object{
        @JvmStatic
        val redisContainer = GenericContainer<Nothing>("redis:7.2.4").apply {
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