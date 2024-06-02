package com.cinephilia.streaming.repository

import com.cinephilia.streaming.domain.enums.Mode
import com.cinephilia.streaming.domain.enums.Quality
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*
import kotlin.time.Duration

@Document("movie-projection")
data class Movie(
    @Id
    val movieId: UUID,
    val putUrl: String,
    val getUrl: String,
)

@Repository
interface MovieRepository : MongoRepository<Movie, UUID> {
    fun findAllByMovieId(movieId: UUID): List<Movie>
}