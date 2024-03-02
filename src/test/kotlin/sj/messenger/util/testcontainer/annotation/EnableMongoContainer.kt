package sj.messenger.util.testcontainer.annotation

import org.springframework.test.context.ContextConfiguration
import sj.messenger.util.testcontainer.initializer.MongoContainerInitializer


@ContextConfiguration(initializers = [MongoContainerInitializer::class])
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class EnableMongoContainer()
