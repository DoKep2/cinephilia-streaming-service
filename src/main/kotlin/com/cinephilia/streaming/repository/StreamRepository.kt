package com.cinephilia.streaming.repository

import com.cinephilia.streaming.domain.enums.Mode
import com.cinephilia.streaming.domain.enums.Quality
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*
import kotlin.time.Duration

@Document("stream-projection")
data class Stream(
    @Id
    val streamId: UUID,
    val movieId: UUID,
    val userId: UUID,
    var playPositionFromStart: String,
    var quality: String,
    var mode: String,
    var createdAt: Long,
)

@Repository
interface StreamRepository : MongoRepository<Stream, UUID> {
    fun findAllByUserIdAndMovieId(userId: UUID, movieId: UUID): List<Stream>
    fun getByUserId(userId: UUID): Stream?
}