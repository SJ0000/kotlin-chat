package sj.messenger.util.testcontainer.initializer

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import sj.messenger.util.testcontainer.TestContainers.Companion.applyRabbitMQProperties
import sj.messenger.util.testcontainer.TestContainers.Companion.rabbitMQContainer

class RabbitMQContainerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {


    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        rabbitMQContainer.start()
        applyRabbitMQProperties(applicationContext.environment)
    }
}