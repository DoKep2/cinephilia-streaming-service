package com.cinephilia.streaming.domain.aggregates

import com.cinephilia.streaming.domain.enums.Quality
import com.cinephilia.streaming.domain.events.*
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.time.Duration


fun StreamAggregateState.start(id: UUID, movieId: UUID, userId: UUID): StreamStartedEvent {
    return StreamStartedEvent(
        streamId = id,
        movieId = movieId,
        userId = userId,
    )
}

fun StreamAggregateState.pause(playPositionFromStart: Duration) : StreamPausedEvent {
    if (playPositionFromStart < Duration.ZERO){
        throw IllegalArgumentException("play position: $playPositionFromStart less than zero")
    }
    return StreamPausedEvent(
        streamId = this.getId(),
        playPositionFromStart = playPositionFromStart
    )
}

fun StreamAggregateState.stop(playPositionFromStart: Duration) : StreamStoppedEvent {
    if (playPositionFromStart < Duration.ZERO){
        throw IllegalArgumentException("play position: $playPositionFromStart less than zero")
    }
    return StreamStoppedEvent(
        streamId = this.getId(),
        playPositionFromStart = playPositionFromStart
    )
}

fun StreamAggregateState.resume() : StreamResumedEvent {
    return StreamResumedEvent(
        streamId = this.getId(),
    )
}

fun StreamAggregateState.seek(offset: Duration) : StreamSoughtEvent {
    var off = offset
    if (this.getPosition() + offset < Duration.ZERO){
        off = -this.getPosition()
    }
    return StreamSoughtEvent(
        streamId = this.getId(),
        offset = off
    )
}

fun StreamAggregateState.sendPlaybackProgress(playPositionFromStart: Duration) : StreamPlaybackProgressEvent {
    if (playPositionFromStart < Duration.ZERO){
        throw IllegalArgumentException("play position: $playPositionFromStart less than zero")
    }
    return StreamPlaybackProgressEvent(
        streamId = this.getId(),
        playPositionFromStart = playPositionFromStart
    )
}

fun StreamAggregateState.changeQuality(quality: Quality) : StreamQualityChangedEvent {
    return StreamQualityChangedEvent(
        streamId = this.getId(),
        quality = quality
    )
}

