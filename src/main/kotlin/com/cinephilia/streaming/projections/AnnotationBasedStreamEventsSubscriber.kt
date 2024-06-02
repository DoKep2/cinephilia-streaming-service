package com.cinephilia.streaming.projections

import com.cinephilia.streaming.domain.aggregates.StreamAggregate
import com.cinephilia.streaming.domain.enums.Mode
import com.cinephilia.streaming.domain.events.*
import com.cinephilia.streaming.repository.Stream
import com.cinephilia.streaming.repository.StreamRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent
import kotlin.time.Duration

@Service
@AggregateSubscriber(
    aggregateClass = StreamAggregate::class, subscriberName = "demo-subs-stream"
)
class AnnotationBasedStreamEventsSubscriber(
    private val streamRepository: StreamRepository,
) {

    val logger: Logger = LoggerFactory.getLogger(AnnotationBasedStreamEventsSubscriber::class.java)

    @SubscribeEvent
    fun taskPausedSubscriber(event: StreamPausedEvent) {
        logger.info("Stream paused: {}", event.streamId)
        val comment = streamRepository.findById(event.streamId).get()
        comment.playPositionFromStart = event.playPositionFromStart.toIsoString()
        comment.mode = Mode.Pause.toString()
        streamRepository.save(comment)
    }

    @SubscribeEvent
    fun streamResumeSubscriber(event: StreamResumedEvent) {
        logger.info("Stream resume: {}", event.streamId)
        val stream = streamRepository.findById(event.streamId).get()
        stream.mode = Mode.Play.toString()
        streamRepository.save(stream)
    }

    @SubscribeEvent
    fun streamPlaybackProgressSubscriber(event: StreamPlaybackProgressEvent) {
        logger.info("Stream PlaybackProgress: {}", event.streamId)
        val stream = streamRepository.findById(event.streamId).get()
        stream.playPositionFromStart = event.playPositionFromStart.toIsoString()
        streamRepository.save(stream)
    }

    @SubscribeEvent
    fun streamSeekSubscriber(event: StreamSoughtEvent) {
        logger.info("Stream sought: {}", event.streamId)
        val stream = streamRepository.findById(event.streamId).get()
        stream.playPositionFromStart = (Duration.parse(stream.playPositionFromStart) + event.offset).toIsoString()
        streamRepository.save(stream)
    }

    @SubscribeEvent
    fun streamQualityChangedSubscriber(event: StreamQualityChangedEvent) {
        logger.info("Stream sought: {}", event.streamId)
        val stream = streamRepository.findById(event.streamId).get()
        stream.quality = event.quality.toString()
        streamRepository.save(stream)
    }

    @SubscribeEvent
    fun streamStartedSubscriber(event: StreamStartedEvent) {
        logger.info("Stream started: {}", event.streamId)
        streamRepository.save(
            Stream(
                streamId = event.streamId,
                movieId =  event.movieId,
                userId = event.userId,
                playPositionFromStart = event.playPositionFromStart.toIsoString(),
                quality =  event.quality.toString(),
                mode = event.mode.toString(),
                createdAt = event.createdAt,
            ),
        )
    }
}