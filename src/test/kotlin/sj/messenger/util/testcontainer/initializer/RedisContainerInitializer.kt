package sj.messenger.util.testcontainer.initializer

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import sj.messenger.util.testcontainer.TestContainers.Companion.applyRedisProperties
import sj.messenger.util.testcontainer.TestContainers.Companion.redisContainer

class RedisContainerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        redisContainer.start()
        applyRedisProperties(applicationContext.environment)
    }
}