package com.cinephilia.streaming.domain.aggregates

import ru.quipy.domain.AggregateState
import com.cinephilia.streaming.domain.enums.Mode
import com.cinephilia.streaming.domain.enums.Quality
import com.cinephilia.streaming.domain.events.*
import ru.quipy.core.annotations.AggregateType
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.Aggregate
import java.util.UUID
import kotlin.time.Duration

@AggregateType(aggregateEventsTableName = "aggregate-stream")
class StreamAggregate : Aggregate {
}

class StreamAggregateState : AggregateState<UUID, StreamAggregate> {
    private lateinit var streamId: UUID
    private lateinit var movieId: UUID
    private lateinit var userId: UUID
    private lateinit var quality: Quality
    private lateinit var mode: Mode
    private var playPositionFromStart: Duration = Duration.ZERO

    override fun getId() = streamId
    fun getPosition() = playPositionFromStart

    @StateTransitionFunc
    fun streamStartedApply(event: StreamStartedEvent) {
        streamId = event.streamId
        movieId = event.movieId
        userId = event.userId
        playPositionFromStart = event.playPositionFromStart
        quality = event.quality
        mode = event.mode
    }

    @StateTransitionFunc
    fun streamStoppedApply(event: StreamStoppedEvent) {
        playPositionFromStart = event.playPositionFromStart
        mode = Mode.Stop
    }

    @StateTransitionFunc
    fun streamPausedApply(event: StreamPausedEvent) {
        playPositionFromStart = event.playPositionFromStart
        mode = Mode.Pause
    }

    @StateTransitionFunc
    fun streamResumedApply(event: StreamResumedEvent) {
        mode = Mode.Play
    }

    @StateTransitionFunc
    fun streamSoughtApply(event: StreamSoughtEvent) {
        playPositionFromStart += event.offset
    }

    @StateTransitionFunc
    fun streamPlaybackProgressApply(event: StreamPlaybackProgressEvent) {
        playPositionFromStart = event.playPositionFromStart
    }

    @StateTransitionFunc
    fun streamQualityChangedApply(event: StreamQualityChangedEvent) {
        quality = event.quality
    }
}

