package sj.messenger.util.testcontainer.annotation

import org.springframework.test.context.ContextConfiguration
import sj.messenger.util.testcontainer.initializer.MySqlContainerInitializer


@ContextConfiguration(initializers = [MySqlContainerInitializer::class])
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class EnableMySqlContainer()