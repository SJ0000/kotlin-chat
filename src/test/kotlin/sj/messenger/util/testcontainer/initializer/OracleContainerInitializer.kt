package sj.messenger.util.testcontainer.initializer

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import sj.messenger.util.testcontainer.TestContainers.Companion.applyOracleProperties
import sj.messenger.util.testcontainer.TestContainers.Companion.oracleContainer

class OracleContainerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        oracleContainer.start()
        applyOracleProperties(applicationContext.environment)
    }
}