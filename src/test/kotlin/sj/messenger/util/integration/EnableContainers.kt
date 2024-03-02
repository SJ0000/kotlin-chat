package sj.messenger.util.integration

import sj.messenger.util.testcontainer.annotation.EnableMongoContainer
import sj.messenger.util.testcontainer.annotation.EnableMySqlContainer
import sj.messenger.util.testcontainer.annotation.EnableRedisContainer

@EnableMySqlContainer
@EnableMongoContainer
@EnableRedisContainer
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class EnableContainers()