package com.cinephilia.streaming.config

import com.cinephilia.streaming.domain.aggregates.*

import com.cinephilia.streaming.projections.AnnotationBasedStreamEventsSubscriber
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.quipy.core.EventSourcingServiceFactory
import ru.quipy.streams.AggregateEventStreamManager
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct

@Configuration
class EventSourcingLibConfiguration {

    private val logger = LoggerFactory.getLogger(EventSourcingLibConfiguration::class.java)

    @Autowired
    private lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @Autowired
    private lateinit var streamEventSubscriber: AnnotationBasedStreamEventsSubscriber

    @Autowired
    private lateinit var eventStreamManager: AggregateEventStreamManager

    @Autowired
    private lateinit var eventSourcingServiceFactory: EventSourcingServiceFactory

    /**
     * Use this object to create/update the aggregate
     */
    @Bean
    fun projectEsService() = eventSourcingServiceFactory.create<UUID, StreamAggregate, StreamAggregateState>()

    @Bean
    fun movieUrlService() = eventSourcingServiceFactory.create<UUID, MovieURLsAggregate, MovieURLsAggregateState>()

    @PostConstruct
    fun init() {
        // Demonstrates how to explicitly subscribe the instance of annotation based subscriber to some stream. See the [AggregateSubscriptionsManager]
        subscriptionsManager.subscribe<StreamAggregate>(streamEventSubscriber)

        // Demonstrates how you can set up the listeners to the event stream
        eventStreamManager.maintenance {
            onRecordHandledSuccessfully { streamName, eventName ->
                logger.info("Stream $streamName successfully processed record of $eventName")
            }

            onBatchRead { streamName, batchSize ->
                logger.info("Stream $streamName read batch size: $batchSize")
            }
        }
    }

}