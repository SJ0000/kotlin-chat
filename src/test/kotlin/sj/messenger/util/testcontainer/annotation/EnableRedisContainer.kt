package sj.messenger.util.testcontainer.annotation

import org.springframework.test.context.ContextConfiguration
import sj.messenger.util.testcontainer.initializer.RedisContainerInitializer


@ContextConfiguration(initializers = [RedisContainerInitializer::class])
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class EnableRedisContainer()
