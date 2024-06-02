package com.cinephilia.streaming.controller

import com.cinephilia.streaming.api.StreamingApi
import com.cinephilia.streaming.consumers.KafkaEventListener
import com.cinephilia.streaming.domain.aggregates.*
import com.cinephilia.streaming.domain.enums.Quality
import com.cinephilia.streaming.mapper.streamStartedEventToStreamResponse
import com.cinephilia.streaming.mapper.streamToStreamResponse
import com.cinephilia.streaming.model.MovieUrls
import com.cinephilia.streaming.model.StreamStartedData
import com.cinephilia.streaming.repository.MovieRepository
import com.cinephilia.streaming.repository.Stream
import com.cinephilia.streaming.repository.StreamRepository
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import ru.quipy.core.EventSourcingService
import java.util.*
import kotlin.math.log
import kotlin.time.Duration
import org.springframework.retry.annotation.Retryable
import org.springframework.security.core.context.SecurityContextHolder
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import com.cinephilia.streaming.security.models.AuthUser

@RestController
@SecurityRequirement(name = "auth")
class StreamingApiImpl(
    val projectEsService: EventSourcingService<UUID, StreamAggregate, StreamAggregateState>,
    val movieEsService: EventSourcingService<UUID, MovieURLsAggregate,MovieURLsAggregateState>,
    val streamProjectionRepository: StreamRepository,
    val movieProjectionRepository: MovieRepository,
)
    : StreamingApi {
    private val logger = LoggerFactory.getLogger(StreamingApi::class.java)
    override fun streamingActionsChangePost(streamId: String, quality: String): ResponseEntity<Unit> {
        projectEsService.update(UUID.fromString(streamId)){it.changeQuality(Quality.valueOf(quality))}
        return ResponseEntity.ok().build()
    }

    override fun streamingActionsPausePost(streamId: String, playPosition: String): ResponseEntity<Unit> {
        projectEsService.update(UUID.fromString(streamId)){it.pause(Duration.parse(playPosition))}
        return ResponseEntity.ok().build()
    }

    override fun streamingActionsResumePost(streamId: String, playPosition: String): ResponseEntity<Unit> {
        projectEsService.update(UUID.fromString(streamId)){it.resume()}
        return ResponseEntity.ok().build()
    }

    override fun streamingActionsSeekPost(streamId: String, offset: String): ResponseEntity<Unit> {
        projectEsService.update(UUID.fromString(streamId)){it.seek(Duration.parse(offset))}
        return ResponseEntity.ok().build()
    }

    override fun streamingActionsSendPost(streamId: String, playPosition: String): ResponseEntity<Unit> {
        projectEsService.update(UUID.fromString(streamId)){it.sendPlaybackProgress(Duration.parse(playPosition))}
        return ResponseEntity.ok().build()
    }

    @Retryable(retryFor = [RuntimeException::class], maxAttempts = 5)
    override fun streamingActionsStartPost(userId: String, movieId: String): ResponseEntity<StreamStartedData> {
        val stream =  streamProjectionRepository.findAllByUserIdAndMovieId(UUID.fromString(userId), UUID.fromString(movieId))
        val data : StreamStartedData = if (stream.isEmpty()){
            val create = projectEsService.create { it.start(UUID.randomUUID(), UUID.fromString(movieId), UUID.fromString(userId)) }
            streamStartedEventToStreamResponse(create)
        } else{
            projectEsService.update(stream.last().streamId) { it.resume() }
            streamToStreamResponse(stream.last())
        }

        return ResponseEntity.ok(data)
    }


    override fun streamingMovieLinksGet(movieId: String): ResponseEntity<MovieUrls> {
        val movie = movieEsService.getState(UUID.fromString(movieId)) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(
            MovieUrls(movie.getURl, movie.putURl, movie.movieId.toString())
        )
    }
//
//    fun streamingUserGetId(): ResponseEntity<List<RecommendedMovie>> {
//        val currentUser = SecurityContextHolder.getContext().authentication.principal as AuthUser
//        logger.info("${currentUser.id}")
//        try {
//            val recommendationsObject =
//                recommendationEsService.getState(currentUser.id) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
//        } finally {
//        }
//    }
}


