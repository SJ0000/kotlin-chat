package sj.messenger.util.repository.annotation

import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest
import org.testcontainers.junit.jupiter.Testcontainers

@DataRedisTest
@Testcontainers
annotation class RedisRepositoryTest()
