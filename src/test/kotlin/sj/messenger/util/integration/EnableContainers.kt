package sj.messenger.util.integration

import org.springframework.test.context.ContextConfiguration
import sj.messenger.util.testcontainer.initializer.*

@ContextConfiguration(initializers = [ContainerParallelInitializer::class])
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class EnableContainers()