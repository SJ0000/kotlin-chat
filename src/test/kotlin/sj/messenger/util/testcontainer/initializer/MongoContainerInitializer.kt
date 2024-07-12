package sj.messenger.util.testcontainer.initializer

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import sj.messenger.util.testcontainer.TestContainers.Companion.applyMongoProperties
import sj.messenger.util.testcontainer.TestContainers.Companion.mongoDBContainer

class MongoContainerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        mongoDBContainer.start()
        applyMongoProperties(applicationContext.environment)
    }
}