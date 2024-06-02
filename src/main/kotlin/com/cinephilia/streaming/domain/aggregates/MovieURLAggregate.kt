package com.cinephilia.streaming.domain.aggregates

import com.cinephilia.streaming.domain.events.*
import ru.quipy.core.annotations.AggregateType
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.Aggregate
import ru.quipy.domain.AggregateState
import java.util.*

@AggregateType(aggregateEventsTableName = "movie-urls")
class MovieURLsAggregate : Aggregate {
}

class MovieURLsAggregateState : AggregateState<UUID, MovieURLsAggregate> {
    lateinit var movieId: UUID
    lateinit var putURl: String
    lateinit var getURl: String

    override fun getId() = movieId

    fun urlsCreated(movie: UUID, getUrl: String, putUrl: String): UrlsCreatedEvent {
        return UrlsCreatedEvent(movie, getUrl, putUrl)
    }

    @StateTransitionFunc
    fun createLinks(event: UrlsCreatedEvent) {
        movieId = event.movieId
        putURl = event.putUrl
        getURl = event.getUrl
    }
}