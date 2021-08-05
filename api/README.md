# Car Service API :car:

## About

Car Service API is a [RESTful API](https://www.redhat.com/en/topics/api/what-is-a-rest-api) developed with
100% [Kotlin](https://kotlinlang.org/) in [Spring framework](https://spring.io/projects/spring-framework) using many
both [Spring Projects](https://spring.io/projects) and other open source projects. It
follows [HATEOAS](https://en.wikipedia.org/wiki/HATEOAS#:~:text=Hypermedia%20as%20the%20Engine%20of,provide%20information%20dynamically%20through%20hypermedia.)
design principle and
uses [JSON Hypertext Application Language](https://datatracker.ietf.org/doc/html/draft-kelly-json-hal-08) (HAL)
specification.

## Technologies Used

### (Some) Spring Projects

+ [Spring Boot](https://spring.io/projects/spring-boot)
+ Spring Configuration Processor
+ [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
+ [Spring Data Redis](https://spring.io/projects/spring-data-redis)
+ [Spring HATEOAS](https://spring.io/projects/spring-hateoas)
+ Spring OAuth2 Resource Server
+ [Spring Security](https://spring.io/projects/spring-security)
+ Spring Validation
+ Spring Web (MVC)
+ Spring WebFlux

### Standalone projects

+ [Flyway Migration](https://flywaydb.org/)
+ [Jackson Project](https://github.com/FasterXML/jackson)
+ [Kotlin Logging](https://github.com/MicroUtils/kotlin-logging)
+ [PostgreSQL](https://www.postgresql.org/)
+ [ShedLock](https://github.com/lukas-krecan/ShedLock)

### Testing

+ [AssertJ](https://github.com/assertj/assertj-core)
+ [JUnit 5](https://junit.org/junit5/)
+ [MockK](https://mockk.io/)
+ [MockServer](https://www.mock-server.com/)
+ Spring Boot Actuator
+ Spring Security Test
+ Spring Test
+ [SpringMockK](https://github.com/Ninja-Squad/springmockk)
+ [Testcontainers](https://www.testcontainers.org/)
+ [TestSets](https://github.com/unbroken-dome/gradle-testsets-plugin)

### DevOps

+ [detekt](https://github.com/detekt/detekt)
+ [Docker](https://www.docker.com/)
+ [Gradle](https://gradle.org/)
+ [JaCoCo](https://www.jacoco.org/jacoco/)
+ [Ktlint](https://github.com/JLLeitschuh/ktlint-gradle)

### Documentation

+ [KDoc](https://kotlinlang.org/docs/kotlin-doc.html)
+ [Postman](https://www.postman.com/) - example Postman collection available [here](./docs/postman)
+ [Swagger](https://swagger.io/) - **very** basic OpenAPI 3.x Specification can be found [here](./docs/swagger)

## Dockerizing :hammer:

Using the Spring Boot's built-in [Paketo Buildpacks](https://paketo.io/):

```shell
$ ./gradlew bootBuildImage --info
```

we are able to generate `soundwalk/car-service:latest` Docker image.
