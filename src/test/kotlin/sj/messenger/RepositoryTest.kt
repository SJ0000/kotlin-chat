package sj.messenger

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import sj.messenger.global.config.QueryDslConfig

@DataJpaTest
@Import(QueryDslConfig::class)
annotation class RepositoryTest()
