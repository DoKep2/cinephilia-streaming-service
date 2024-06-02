package com.cinephilia.streaming.domain.events

import com.cinephilia.streaming.domain.aggregates.StreamAggregate
import com.cinephilia.streaming.domain.enums.Mode
import com.cinephilia.streaming.domain.enums.Quality
import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*
import kotlin.time.Duration

const val STREAM_STARTED_EVENT =
    "STREAM_STARTED_EVENT" //  событие, которое указывает, что стриминг для определенного контента начался.
const val STREAM_STOPPED_EVENT =
    "STREAM_STOPPED_EVENT" // событие, которое указывает, что стриминг для определенного контента был остановлен.
const val STREAM_PAUSED_EVENT =
    "STREAM_PAUSED_EVENT" // событие, которое указывает, что стриминг для определенного контента был приостановлен.
const val STREAM_RESUMED_EVENT =
    "STREAM_RESUMED_EVENT" // событие, которое указывает, что стриминг для определенного контента был возобновлен после паузы.
const val STREAM_SOUGHT_EVENT =
    "STREAM_SEEKED_EVENT" // событие, которое указывает, что пользователь произвел перемотку назад или вперед во время просмотра стрима.
const val STREAM_PLAYBACK_PROGRESS_EVENT =
    "STREAM_PLAYBACK_PROGRESS_EVENT" // событие, которое указывает текущий прогресс воспроизведения контента (время в секундах от начала воспроизведения).
const val STREAM_QUALITY_CHANGED_EVENT =
    "STREAM_QUALITY_CHANGED_EVENT" // событие, которое указывает, что качесвто стриминга изменилось.

@DomainEvent(name = STREAM_STARTED_EVENT)
class StreamStartedEvent(
    val streamId: UUID,
    val movieId: UUID,
    val userId: UUID,
    val playPositionFromStart: Duration = Duration.ZERO,
    val quality: Quality = Quality.FHD,
    val mode: Mode = Mode.Play,
    createdAt: Long = System.currentTimeMillis(),
) : Event<StreamAggregate>(
    name = STREAM_STARTED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = STREAM_STOPPED_EVENT)
class StreamStoppedEvent(
    val streamId: UUID,
    val playPositionFromStart: Duration,
    createdAt: Long = System.currentTimeMillis(),
) : Event<StreamAggregate>(
    name = STREAM_STOPPED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = STREAM_PAUSED_EVENT)
class StreamPausedEvent(
    val streamId: UUID,
    val playPositionFromStart: Duration,
    createdAt: Long = System.currentTimeMillis(),
) : Event<StreamAggregate>(
    name = STREAM_PAUSED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = STREAM_RESUMED_EVENT)
class StreamResumedEvent(
    val streamId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<StreamAggregate>(
    name = STREAM_RESUMED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = STREAM_SOUGHT_EVENT)
class StreamSoughtEvent(
    val streamId: UUID,
    val offset: Duration,
    createdAt: Long = System.currentTimeMillis(),
) : Event<StreamAggregate>(
    name = STREAM_SOUGHT_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = STREAM_PLAYBACK_PROGRESS_EVENT)
class StreamPlaybackProgressEvent(
    val streamId: UUID,
    val playPositionFromStart: Duration,
    createdAt: Long = System.currentTimeMillis(),
) : Event<StreamAggregate>(
    name = STREAM_PLAYBACK_PROGRESS_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = STREAM_QUALITY_CHANGED_EVENT)
class StreamQualityChangedEvent(
    val streamId: UUID,
    val quality: Quality,
    createdAt: Long = System.currentTimeMillis(),
) : Event<StreamAggregate>(
    name = STREAM_QUALITY_CHANGED_EVENT,
    createdAt = createdAt,
)
