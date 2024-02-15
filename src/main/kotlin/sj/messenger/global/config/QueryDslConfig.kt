package sj.messenger.global.config

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
class QueryDslConfig(
) {
    @Bean
    fun jpaQueryFactory(entityManager: EntityManager) : JPAQueryFactory{
        return JPAQueryFactory(entityManager)
    }
}