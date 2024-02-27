package sj.messenger.util

import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.*
import sj.messenger.global.domain.BaseEntity

fun assertEntityLoaded(em : EntityManager, vararg entities : BaseEntity){
    val result = entities.map { em.entityManagerFactory.persistenceUnitUtil.isLoaded(it) }
    assertThat(result).allMatch { it }
}