import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val registryUrl: String by project

plugins {
	kotlin("jvm") version "1.9.20"
	id("org.springframework.boot") version "3.1.5"
	kotlin("plugin.spring") version "1.9.20"
	id("org.openapi.generator") version "6.6.0"
}

group = "com.cinephilia"
version = "1.0.0"

repositories {
	mavenCentral()
}

dependencies {


	implementation("org.springframework.retry:spring-retry:2.0.3")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.20")

	implementation("ru.quipy:tiny-event-sourcing-spring-boot-starter:2.3.0")
	implementation("org.springframework.boot:spring-boot-starter-web:2.7.7")
	implementation("org.springdoc:springdoc-openapi-ui:1.7.0")

	implementation("ru.quipy:tiny-mongo-event-store-spring-boot-starter:2.3.0")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb:2.7.7")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.3")
	implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.20")

	implementation("io.minio:minio:8.5.7")

	compileOnly("org.springframework.boot:spring-boot-devtools:2.7.5")

	implementation("jakarta.validation:jakarta.validation-api")
	implementation("jakarta.annotation:jakarta.annotation-api:2.1.0")

	testImplementation("org.springframework.boot:spring-boot-starter-test:2.7.7")
	implementation("javax.validation:validation-api:2.0.1.Final")
	implementation("javax.annotation:javax.annotation-api:1.3.2")
	implementation("com.beust:klaxon:5.6")
	implementation ("org.springframework.kafka:spring-kafka:2.9.9")
	implementation("org.springframework.boot:spring-boot-starter-security:2.7.7")
	implementation("org.springframework.security:spring-security-core:5.7.4")
	implementation("org.springframework.security:spring-security-config:5.7.4")
	implementation("org.springframework.security:spring-security-web:3.1.4.RELEASE")
	implementation("io.jsonwebtoken:jjwt-api:0.11.2")
	implementation("io.jsonwebtoken:jjwt-impl:0.11.2")
	implementation("io.jsonwebtoken:jjwt-jackson:0.11.2")
	implementation("io.github.resilience4j:resilience4j-circuitbreaker:2.2.0")
	implementation("com.auth0:java-jwt:4.4.0")
	implementation("org.apache.commons:commons-vfs2:2.9.0")
	implementation ("org.aspectj:aspectjrt:1.9.7")
	implementation("org.springframework.boot:spring-boot-starter-aop:2.7.7")
//	implementation("io.opentelemetry:opentelemetry-bom:1.37.0")
	implementation("io.opentelemetry:opentelemetry-api:1.37.0")
	implementation("io.opentelemetry:opentelemetry-sdk:1.37.0")
	implementation("io.opentelemetry:opentelemetry-exporter-otlp:1.37.0")
//	implementation("io.opentelemetry:opentelemetry-bom-derived-enforced-platform:1.37.0")
	implementation("io.opentelemetry.instrumentation:opentelemetry-spring-boot-starter:2.3.0-alpha")
	implementation("io.opentelemetry.instrumentation:opentelemetry-java-http-client:2.3.0-alpha")
}

apply { plugin("org.openapi.generator") }

openApiGenerate {
	generatorName.set("kotlin-spring")
	inputSpec.set("$rootDir/src/main/resources/streaming_api_description.yaml")
	outputDir.set("$rootDir/src/main/kotlin/generated")
	apiPackage.set("com.cinephilia.streaming.api")
	modelPackage.set("com.cinephilia.streaming.model")
	generateModelTests.set(false)
	generateApiTests.set(false)
	configOptions.set(
		mapOf(
			"interfaceOnly" to "true",
		)
	)
}

tasks.withType<KotlinCompile> {
	dependsOn(tasks.openApiGenerate)
	kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
	kotlinOptions.javaParameters = true
	kotlinOptions.freeCompilerArgs += "-Xjsr305=strict"
	kotlinOptions.freeCompilerArgs += "-java-parameters"
}

tasks.test {
	useJUnitPlatform()
}

kotlin {
	jvmToolchain(17)
}

allOpen {
	annotation("org.springframework.boot.autoconfigure.SpringBootApplication")
	annotation("org.springframework.context.annotation.Configuration")
}