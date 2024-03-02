package sj.messenger.util.testcontainer.initializer

import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext

class MySqlContainerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    companion object{
        const val MYSQL_CONTAINER_VERSION = "8.0.36"
        const val DRIVER_CLASS_NAME = "org.testcontainers.jdbc.ContainerDatabaseDriver"
    }

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        val jdbcUrl = "jdbc:tc:mysql:${MYSQL_CONTAINER_VERSION}:///test"
        TestPropertyValues.of(
            "spring.datasource.url=${jdbcUrl}",
            "spring.datasource.username=sa",
            "spring.datasource.password=test",
            "spring.datasource.driver-class-name=${DRIVER_CLASS_NAME}",
        ).applyTo(applicationContext.environment)
    }
}