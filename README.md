# Dropwizard Sample Application

## Running

### Run on local JVM

when run the service on your local JVM, set *program argument* as:

```
server config.develop.yaml
```

second, execute:

```
docker-compose up --build
```

then run `com.example.sample.SampleApplication` class.

### Run with Docker Compose

when run the application with Docker Compose, execute:

```
docker-compose -f docker-compose.test.yaml up --build
```
