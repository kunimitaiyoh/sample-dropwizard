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

## Database Migration manually

```
docker-compose -f docker-compose.test.yaml build
docker-compose -f docker-compose.test.yaml run rest bash
java -jar target/sample.jar db migrate config.test.yaml
```

For more information about migration, see [Dropwizard Migrations](http://www.dropwizard.io/1.1.0/docs/manual/migrations.html).
