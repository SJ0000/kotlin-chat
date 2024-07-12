package sj.messenger.util.testcontainer.initializer


import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.lifecycle.Startables
import sj.messenger.util.testcontainer.TestContainers.Companion.applyMongoProperties
import sj.messenger.util.testcontainer.TestContainers.Companion.applyOracleProperties
import sj.messenger.util.testcontainer.TestContainers.Companion.applyRabbitMQProperties
import sj.messenger.util.testcontainer.TestContainers.Companion.applyRedisProperties
import sj.messenger.util.testcontainer.TestContainers.Companion.mongoDBContainer
import sj.messenger.util.testcontainer.TestContainers.Companion.oracleContainer
import sj.messenger.util.testcontainer.TestContainers.Companion.rabbitMQContainer
import sj.messenger.util.testcontainer.TestContainers.Companion.redisContainer


class ContainerParallelInitializer  : ApplicationContextInitializer<ConfigurableApplicationContext> {

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        Startables.deepStart(oracleContainer, mongoDBContainer, redisContainer, rabbitMQContainer).join()
        applyOracleProperties(applicationContext.environment)
        applyMongoProperties(applicationContext.environment)
        applyRedisProperties(applicationContext.environment)
        applyRabbitMQProperties(applicationContext.environment)
    }

}