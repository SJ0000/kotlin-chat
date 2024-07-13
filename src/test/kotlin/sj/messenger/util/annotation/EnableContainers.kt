package sj.messenger.util.annotation

import org.springframework.test.context.ContextConfiguration
import sj.messenger.util.testcontainer.initializer.ContainerParallelInitializer

@ContextConfiguration(initializers = [ContainerParallelInitializer::class])
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class EnableContainers()