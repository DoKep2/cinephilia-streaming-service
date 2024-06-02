package com.cinephilia.streaming.consumers

import com.beust.klaxon.Klaxon
import com.cinephilia.streaming.domain.aggregates.MovieURLsAggregate
import com.cinephilia.streaming.domain.aggregates.MovieURLsAggregateState
import io.minio.GetPresignedObjectUrlArgs
import io.minio.MinioClient
import io.minio.errors.MinioException
import io.minio.http.Method
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import ru.quipy.core.EventSourcingService
import java.util.*
import java.util.concurrent.TimeUnit


data class MovieCreatedMessage(
    val movieId: String,
)

@Component
class KafkaEventListener(
    @Value("\${application.MINIO_ENDPOINT}") private val endpoint: String,
    @Value("\${application.MINIO_ACCESS_KEY}") private val accessKey: String,
    @Value("\${application.MINIO_SECRET_KEY}") private val secretKey: String,
    val movieEsService: EventSourcingService<UUID, MovieURLsAggregate, MovieURLsAggregateState>,
) {
    private val logger = LoggerFactory.getLogger(KafkaEventListener::class.java)
//    private final val endpoint: String = System.getenv("MINIO_ENDPOINT")
//    private final val accessKey: String = System.getenv("MINIO_ACCESS_KEY")
//    private final val secretKey: String = System.getenv("MINIO_SECRET_KEY")
    private final var minioClient: MinioClient? = null

    init {
        try {
            // Create a minioClient with the MinIO server playground, its access key and secret key.
            minioClient =
                MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();
        } catch (e: MinioException) {
            println("Error occurred: " + e);
            println("HTTP trace: " + e.httpTrace());
        }
    }

    @KafkaListener(id = "streaming-event-listener", containerFactory = "kafkaListenerContainerFactory", topics = ["cinephilia.movies.created"])
    fun listenMovieRated(record : ConsumerRecord<String, String> ) {
        try {
            logger.info(record.value())
            val event = Klaxon().parse<MovieCreatedMessage>(record.value())
            if (event == null) {
                logger.error("Can't deserealize message ${record.key()} ${record.value()}, skip")
                return
            }

            logger.info("pobeda")

            if (minioClient != null) {
                // Create uploaded url for movie
                val putUrl = minioClient!!.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                        .method(Method.PUT)
                        .bucket("movies")
                        .`object`(event.movieId)
                        .expiry(12, TimeUnit.HOURS)
                        .build()
                )

                val getUrl = minioClient!!.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket("movies")
                        .`object`(event.movieId)
                        .expiry(7, TimeUnit.DAYS)
                        .build()
                )

                movieEsService.create { it.urlsCreated(UUID.fromString(event.movieId), getUrl, putUrl) }
                logger.info(getUrl)
                logger.info(putUrl)
            }

        } catch (e: Exception) {
            logger.error("Error on consuming message ${record.key()} ${record.value()} - ${e.message}")
        }
    }
}