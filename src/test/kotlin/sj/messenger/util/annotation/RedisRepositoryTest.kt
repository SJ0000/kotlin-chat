package sj.messenger.util.annotation

import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest
import sj.messenger.util.testcontainer.annotation.EnableRedisContainer


@DataRedisTest
@EnableRedisContainer
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class RedisRepositoryTest()
