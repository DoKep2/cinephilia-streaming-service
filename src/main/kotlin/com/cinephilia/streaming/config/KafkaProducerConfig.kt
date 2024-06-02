package com.cinephilia.streaming.config

import com.cinephilia.streaming.domain.events.UrlsCreatedEvent
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.KafkaListenerContainerFactory
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer


class StreamingTopicConfiguration {
    companion object {
        const val NAME = "cinephilia.streaming"
        const val PARTITION_COUNT = 1
        const val REPLICATION_FACTOR = 1
    }
}

@Configuration
class KafkaProducerConfig {

    @Value("\${spring.kafka.bootstrap-servers}")
    private lateinit var bootstrapServers: String

    @Bean(name = ["UrlsGeneratedEvent_PRODUCER"])
    fun kafkaTemplatePutUrlGeneratedEvent(): KafkaTemplate<String, UrlsCreatedEvent> {
        val config = mapOf(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java,
        )
        return KafkaTemplate(DefaultKafkaProducerFactory(config))
    }

    @Bean
    fun streamingTopic(): NewTopic {
        return TopicBuilder
                .name(StreamingTopicConfiguration.NAME)
                .partitions(StreamingTopicConfiguration.PARTITION_COUNT)
                .replicas(StreamingTopicConfiguration.REPLICATION_FACTOR)
                .build()
    }

    @Bean
    fun kafkaListenerContainerFactory(): KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> {
        val factory =
            ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.setConsumerFactory(consumerFactory())
        factory.setConcurrency(3)
        factory.containerProperties.pollTimeout = 3000
        return factory
    }

    @Bean
    fun consumerFactory(): ConsumerFactory<String, String> {
        val config = mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java,
        )

        return DefaultKafkaConsumerFactory(config)
    }
}
