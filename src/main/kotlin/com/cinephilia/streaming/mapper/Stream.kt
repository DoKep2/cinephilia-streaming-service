package com.cinephilia.streaming.mapper;

import com.cinephilia.streaming.domain.events.StreamResumedEvent
import com.cinephilia.streaming.domain.events.StreamStartedEvent
import com.cinephilia.streaming.model.StreamStartedData
import com.cinephilia.streaming.repository.Stream

fun streamToStreamResponse(
        comment: Stream,
        ): StreamStartedData {

        return StreamStartedData(
                comment.streamId.toString(),
                comment.playPositionFromStart,
                comment.quality.toString(),
                comment.mode.toString()
        )
}

fun streamStartedEventToStreamResponse(
        comment: StreamStartedEvent,
): StreamStartedData {
        return StreamStartedData(
                comment.streamId.toString(),
                comment.playPositionFromStart.toIsoString(),
                comment.quality.toString(),
                comment.mode.toString()
        )
}