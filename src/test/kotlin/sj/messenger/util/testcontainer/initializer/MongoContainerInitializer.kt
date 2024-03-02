package sj.messenger.util.testcontainer.initializer

import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.MongoDBContainer

class MongoContainerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    companion object{
        @JvmStatic
        val mongoDBContainer: MongoDBContainer = MongoDBContainer("mongo:latest")
            .withExposedPorts(27017)
            .withReuse(true)
    }

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        mongoDBContainer.start()
        TestPropertyValues.of(
            "spring.data.mongodb.host=${mongoDBContainer.host}",
            "spring.data.mongodb.port=${mongoDBContainer.getMappedPort(27017)}",
        ).applyTo(applicationContext.environment)
    }
}