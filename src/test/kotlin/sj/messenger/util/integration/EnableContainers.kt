package sj.messenger.util.integration

import org.springframework.test.context.ContextConfiguration
import sj.messenger.util.testcontainer.initializer.MongoContainerInitializer
import sj.messenger.util.testcontainer.initializer.OracleContainerInitializer
import sj.messenger.util.testcontainer.initializer.RabbitMQContainerInitializer
import sj.messenger.util.testcontainer.initializer.RedisContainerInitializer

@ContextConfiguration(
    initializers = [
        OracleContainerInitializer::class,
        MongoContainerInitializer::class,
        RedisContainerInitializer::class,
        RabbitMQContainerInitializer::class
    ]
)
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class EnableContainers()