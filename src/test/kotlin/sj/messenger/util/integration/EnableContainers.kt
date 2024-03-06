package sj.messenger.util.integration

import org.springframework.test.context.ContextConfiguration
import sj.messenger.util.testcontainer.initializer.MongoContainerInitializer
import sj.messenger.util.testcontainer.initializer.MySqlContainerInitializer
import sj.messenger.util.testcontainer.initializer.RedisContainerInitializer

@ContextConfiguration(
    initializers = [
        MySqlContainerInitializer::class,
        MongoContainerInitializer::class,
        RedisContainerInitializer::class
    ]
)
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class EnableContainers()