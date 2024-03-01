package sj.messenger

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.testcontainers.junit.jupiter.Testcontainers
import sj.messenger.global.config.QueryDslConfig

@DataJpaTest
@Import(QueryDslConfig::class)
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
annotation class RepositoryTest()
