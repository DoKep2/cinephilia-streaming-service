package com.cinephilia.streaming.domain.aggregates

import ru.quipy.core.annotations.AggregateType
import ru.quipy.domain.Aggregate

@AggregateType(aggregateEventsTableName = "movie")
class MovieAggregate : Aggregate {
}
