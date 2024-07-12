package sj.messenger.util.testcontainer.initializer

import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.OracleContainer

class OracleContainerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    companion object{
        private const val DATABASE_NAME = "testDB"
        private const val USER_NAME = "testUser"
        private const val PASSWORD = "testPassword"
        private const val DRIVER_CLASS_NAME = "oracle.jdbc.driver.OracleDriver"
        private const val PORT = 1521
        @JvmStatic
        val oracleContainer : OracleContainer = OracleContainer("gvenzl/oracle-xe:21-slim-faststart")
            .withDatabaseName(DATABASE_NAME)
            .withUsername(USER_NAME)
            .withPassword(PASSWORD)
            .withExposedPorts(PORT)
    }

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        oracleContainer.start()
        val jdbcUrl = "jdbc:oracle:thin:@//${oracleContainer.host}:${oracleContainer.oraclePort}/${DATABASE_NAME}"
        TestPropertyValues.of(
            "spring.datasource.url=${jdbcUrl}",
            "spring.datasource.username=${oracleContainer.username}",
            "spring.datasource.password=${oracleContainer.password}",
            "spring.datasource.driver-class-name=${DRIVER_CLASS_NAME}",
        ).applyTo(applicationContext.environment)
    }
}