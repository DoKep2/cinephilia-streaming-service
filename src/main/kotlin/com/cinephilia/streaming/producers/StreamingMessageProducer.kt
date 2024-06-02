package com.cinephilia.streaming.producers

import com.cinephilia.streaming.config.StreamingTopicConfiguration
import com.cinephilia.streaming.domain.events.UrlsCreatedEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class StreamingMessageProducer {

    @Autowired
    private lateinit var messagePutUrlCreated: KafkaTemplate<String, UrlsCreatedEvent>

    fun sendPutUrlCreatedMessage(message: UrlsCreatedEvent) {
        messagePutUrlCreated.send(StreamingTopicConfiguration.NAME, message)
    }

}