# Car Service API - TL;DR

Technologies required:

- [Java 11 SDK](https://openjdk.java.net/projects/jdk/11/) (at minimum)
- [Docker 20](https://docs.docker.com/get-docker/) (should work with recent versions)
- [Python 3.x](https://www.python.org/downloads/)

### Running the Car Service API:

#### 1. Run External API:

```shell
$ cd ./external && python3 -m http.server 8008
```

#### 2. Build OAuth2.0 Resource Server Docker Image:

```shell
$ cd ./oauth2resourceserver && ./gradlew bootBuildImage
```

#### 3. Build Car Service API Docker Image:

```shell
$ cd ./api && ./gradlew bootBuildImage
```

#### 4. Run Docker Compose:

```shell
$ cd ./api/etc/docker && docker-compose up
```

# Car Service API :car:

Click [here](./api) to learn more about Car Service API (main project).

Links to subprojects required to run the Car Service API:

- [External API](./external)
- [OAuth2.0 Resource Server](./oauth2resourceserver)
