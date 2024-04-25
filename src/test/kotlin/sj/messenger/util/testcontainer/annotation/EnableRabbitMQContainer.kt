package sj.messenger.util.testcontainer.annotation

import org.springframework.test.context.ContextConfiguration
import sj.messenger.util.testcontainer.initializer.RabbitMQContainerInitializer


@ContextConfiguration(initializers = [RabbitMQContainerInitializer::class])
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class EnableRabbitMQContainer()
