package sj.messenger.util.testcontainer

import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.core.env.ConfigurableEnvironment
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.containers.OracleContainer

class TestContainers {

    companion object{
        @JvmStatic
        val mongoDBContainer: MongoDBContainer = MongoDBContainer("mongo:7.0.6")
            .withExposedPorts(27017)
            .withReuse(true)

        private const val DRIVER_CLASS_NAME = "oracle.jdbc.OracleDriver"
        private const val DATABASE_NAME = "testDB"
        @JvmStatic
        val oracleContainer : OracleContainer = OracleContainer("gvenzl/oracle-xe:21-slim-faststart")
            .withDatabaseName(DATABASE_NAME)
            .withUsername("testUser")
            .withPassword("testPassword")
            .withExposedPorts(1521)

        @JvmStatic
        val rabbitMQContainer = GenericContainer<Nothing>("rabbitmq:3.13.1").apply {
            withExposedPorts(5672)
        }

        @JvmStatic
        val redisContainer = GenericContainer<Nothing>("redis:7.2.4").apply {
            withExposedPorts(6379)
        }

        fun applyMongoProperties(environment: ConfigurableEnvironment){
            TestPropertyValues.of(
                "spring.data.mongodb.host=${mongoDBContainer.host}",
                "spring.data.mongodb.port=${mongoDBContainer.getMappedPort(27017)}",
            ).applyTo(environment)
        }

        fun applyOracleProperties(environment: ConfigurableEnvironment){
            val jdbcUrl = "jdbc:oracle:thin:@//${oracleContainer.host}:${oracleContainer.oraclePort}/${DATABASE_NAME}"
            TestPropertyValues.of(
                "spring.datasource.url=${jdbcUrl}",
                "spring.datasource.username=${oracleContainer.username}",
                "spring.datasource.password=${oracleContainer.password}",
                "spring.datasource.driver-class-name=${DRIVER_CLASS_NAME}",
            ).applyTo(environment)
        }

        fun applyRedisProperties(environment: ConfigurableEnvironment){
            TestPropertyValues.of(
                "spring.data.redis.host=${redisContainer.host}",
                "spring.data.redis.port=${redisContainer.getMappedPort(6379)}",
            ).applyTo(environment)
        }

        fun applyRabbitMQProperties(environment: ConfigurableEnvironment){
            TestPropertyValues.of(
                "spring.rabbitmq.host=${rabbitMQContainer.host}",
                "spring.rabbitmq.port=${rabbitMQContainer.getMappedPort(5672)}",
                "spring.rabbitmq.listener.simple.consumer-batch-enabled=${true}",
                "spring.rabbitmq.listener.simple.batch-size=${30}",
            ).applyTo(environment)
        }
    }
}