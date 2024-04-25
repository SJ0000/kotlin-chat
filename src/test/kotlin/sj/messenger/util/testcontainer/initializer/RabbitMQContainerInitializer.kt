package sj.messenger.util.testcontainer.initializer

import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.GenericContainer

class RabbitMQContainerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    companion object{
        @JvmStatic
        val rabbitMQContainer = GenericContainer<Nothing>("rabbitmq:3.13.1").apply {
            withExposedPorts(5672)
        }
    }

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        rabbitMQContainer.start()
        TestPropertyValues.of(
            "spring.rabbitmq.host=${rabbitMQContainer.host}",
            "spring.rabbitmq.port=${rabbitMQContainer.getMappedPort(5672)}",
            "spring.rabbitmq.listener.simple.consumer-batch-enabled=${true}",
            "spring.rabbitmq.listener.simple.batch-size=${30}",
        ).applyTo(applicationContext.environment)
    }
}