<p align="center">
   <img 
     src="https://socialify.git.ci/bendavies99/metrics/image?description=1&font=Source%20Code%20Pro&language=1&owner=0&pattern=Floating%20Cogs&theme=Dark" 
     alt="Metrics" 
     width="640" 
     height="320" />
</p>

# Getting Started

## First Time Running

This REST API uses API-Key authentication so when running the application
for the first time there will be a KEY in the logs that will only display once make sure to keep this safe as
it cannot be recovered without looking at the Database

**Note:** For Dev it uses H2 In-Memory Database so the key will refresh on every application restart

## Building

```shell
  ./gradlew build
```

## Packaging

```shell
  ./gradlew bootJar
```

## Running for Dev

```shell
  ./gradlew bootRun
```

**NOTE:** This will use an H2 In-Memory Database

## Testing

```shell
  ./gradlew test
```

## Docker

### Prerequisites

- Docker
- MySQL Server (accessible to the docker container)
- Build The Docker Image (as seen below)

### Build Docker Image

```shell
  ./gradlew bootBuildImage --imageName=bendavies99/metrics
```

### Docker Command

```shell
  docker run -d -p 8080:8080 \
             -e spring.datasource.url='jdbc:mysql://<ip goes here>:3306/metrics' \
             -e spring.datasource.driver-class-name='com.mysql.cj.jdbc.Driver' \
             -e spring.datasource.username='<user goes here>' \
             -e spring.datasource.password='<password goes here>' \
             -e spring.jpa.hibernate.ddl-auto='update' \
             --restart=always \
             --name=metrics \
             bendavies99/metrics
```

### Docker Compose

```shell
   docker-compose -f ./src/main/docker/docker-compose.yml -p metrics up -d 
```

**NOTE:** It is Recommended that you edit the passwords in this docker-compose file
