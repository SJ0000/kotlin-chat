package sj.messenger.util.annotation

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import sj.messenger.global.config.JpaAuditingConfig
import sj.messenger.global.config.QueryDslConfig
import sj.messenger.util.testcontainer.annotation.EnableOracleContainer

@DataJpaTest
@Import(QueryDslConfig::class, JpaAuditingConfig::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnableOracleContainer
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class JpaRepositoryTest()
