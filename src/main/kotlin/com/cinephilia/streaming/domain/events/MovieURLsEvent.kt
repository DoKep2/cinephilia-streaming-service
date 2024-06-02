package com.cinephilia.streaming.domain.events

import com.cinephilia.streaming.domain.aggregates.MovieURLsAggregate
import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val URLS_CREATED_EVENT: String = "PUT_URL_CREATED_EVENT"
@DomainEvent(name = URLS_CREATED_EVENT)
class UrlsCreatedEvent(
    val movieId: UUID,
    val getUrl: String,
    val putUrl: String,
) : Event<MovieURLsAggregate>(
    name = URLS_CREATED_EVENT,
    createdAt = java.time.Clock
        .systemUTC()
        .millis(),
)