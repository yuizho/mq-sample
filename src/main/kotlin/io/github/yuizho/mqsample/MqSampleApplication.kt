package io.github.yuizho.mqsample

import com.amazon.sqs.javamessaging.ProviderConfiguration
import com.amazon.sqs.javamessaging.SQSConnectionFactory
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.sqs.AmazonSQSClientBuilder
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.jms.annotation.EnableJms
import org.springframework.jms.config.DefaultJmsListenerContainerFactory
import org.springframework.jms.core.JmsTemplate
import org.springframework.jms.support.destination.DynamicDestinationResolver
import javax.jms.Session


@SpringBootApplication
@EnableJms
class MqSampleApplication

fun main(args: Array<String>) {
    runApplication<MqSampleApplication>(*args)
}

fun sqsConnectionFactory(environment: Environment): SQSConnectionFactory {
    var serviceEndpoint = environment.getProperty("mq.service.endpoint") ?: throw RuntimeException("there is no mq.service.endpoint properties")
    val client = AmazonSQSClientBuilder.standard()
            .withCredentials(DefaultAWSCredentialsProviderChain())
            .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(
                    serviceEndpoint,
                    "test-elastic-mq"
            )).build()
    // これしないと、AWS.SimpleQueueService.NonExistentQueue エラーとなった
    // ココで作らなくても、aws-cliとかで事前に作っておけば、jmsTemplateなどで指定した名前があってれば動く
    client.createQueue("mailbox")
    val connectionFactory = SQSConnectionFactory(
            ProviderConfiguration(),
            client
    )
    return connectionFactory
}

@Configuration
class MqSampleConfig(val environment: Environment) {
    @Bean
    fun defaultJmsTemplate(): JmsTemplate = JmsTemplate(sqsConnectionFactory(environment))

    @Bean
    fun jmsListenerContainerFactory(): DefaultJmsListenerContainerFactory {
        return with(DefaultJmsListenerContainerFactory()) {
            setConnectionFactory(sqsConnectionFactory(environment))
            setDestinationResolver(DynamicDestinationResolver())
            setConcurrency("3-10")
            setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE)
            this
        }
    }
}