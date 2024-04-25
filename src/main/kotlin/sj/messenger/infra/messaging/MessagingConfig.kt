package sj.messenger.infra.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.batch.SimpleBatchingStrategy
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.BatchingRabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

@Configuration
class AmqpConfig{

    @Value("\${spring.rabbitmq.listener.simple.batch-size}")
    val batchSize: Int = 0

    @Bean
    fun batchingRabbitTemplate(connectionFactory: ConnectionFactory, amqpMessageConverter: MessageConverter) : BatchingRabbitTemplate{
        val taskScheduler = ThreadPoolTaskScheduler()
        taskScheduler.poolSize = 1
        taskScheduler.initialize()
        val template = BatchingRabbitTemplate(connectionFactory, SimpleBatchingStrategy(batchSize,batchSize * 2,5000),taskScheduler)
        template.messageConverter = amqpMessageConverter
        return template
    }

    @Bean
    fun amqpMessageConverter(objectMapper: ObjectMapper) : MessageConverter{
        return Jackson2JsonMessageConverter(objectMapper)
    }

    @Bean
    fun groupMessageSaveQueue() : Queue = Queue("groupMessageSaveQueue")

    @Bean
    fun directMessageSaveQueue() : Queue = Queue("directMessageSaveQueue")
}