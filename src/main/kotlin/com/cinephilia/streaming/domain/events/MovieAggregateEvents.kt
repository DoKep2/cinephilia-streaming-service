package com.cinephilia.streaming.domain.events

import com.cinephilia.streaming.domain.aggregates.MovieAggregate
import com.cinephilia.streaming.domain.enums.MovieGenre
import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*
import kotlin.time.Duration


const val MOVIE_CREATED_EVENT: String = "MOVIE_CREATED_EVENT"
const val MOVIE_UPLOADED_EVENT: String = "MOVIE_UPLOADED_EVENT"
@DomainEvent(name = MOVIE_CREATED_EVENT)
data class MovieCreatedEvent(
    val movieId: UUID,
    val movieName: String,
    val year: Int,
    val description: String,
    val genres: MutableList<MovieGenre>,
    val price: Float,
    val duration: Int = 0,
    val fileLink: String,
) : Event<MovieAggregate>(
    name = MOVIE_CREATED_EVENT,
    createdAt = java.time.Clock
        .systemUTC()
        .millis()
)

@DomainEvent(name = MOVIE_UPLOADED_EVENT)
data class MovieUploadedEvent(
    val movieId: UUID,
) : Event<MovieAggregate>(
    name = MOVIE_UPLOADED_EVENT,
    createdAt = java.time.Clock
        .systemUTC()
        .millis()
)
