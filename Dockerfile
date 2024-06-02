FROM openjdk:17

RUN mkdir -p /app && \
    chown 1001:0 /app && \
    rm -rf /var/lib/apt/lists/*

USER 1001

COPY build/libs/streaming-1.0.0.jar /app/streaming-1.0.0.jar
ENTRYPOINT ["java","-jar","/app/streaming-1.0.0.jar"]