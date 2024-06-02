package com.cinephilia.streaming.security

import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.instrumentation.httpclient.JavaHttpClientTelemetry
import org.springframework.stereotype.Component
import java.net.http.HttpClient

@Component
class HttpClientFactory(private val openTelemetry: OpenTelemetry) {
    fun newTracedClient(httpClientBuilder: HttpClient.Builder = HttpClient.newBuilder()): HttpClient {
        return JavaHttpClientTelemetry.builder(openTelemetry).build().newHttpClient(httpClientBuilder.build())
    }
}