package com.example.sample

import java.time.Instant

import com.datasift.dropwizard.scala.jdbi.tweak.ProductResultSetMapperFactory
import com.example.sample.core.InstantSerializer
import com.example.sample.dao.RawJdbiUserDao
import com.example.sample.resources.UsersResource
import com.fasterxml.jackson.databind.module.SimpleModule
import io.dropwizard.Application
import io.dropwizard.db.DataSourceFactory
import io.dropwizard.jdbi.DBIFactory
import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper
import io.dropwizard.migrations.MigrationsBundle
import io.dropwizard.setup.{Bootstrap, Environment}

object SampleApplication extends Application[SampleConfig] {
  def main(args: Array[String]): Unit = {
    run(args:_*)
  }

  override def initialize(bootstrap: Bootstrap[SampleConfig]): Unit = {
    bootstrap.addBundle(new MigrationsBundle[SampleConfig] {
      override def getDataSourceFactory(configuration: SampleConfig): DataSourceFactory = configuration.database

      override def getMigrationsFileName: String = "migrations.sql"
    })

    bootstrap.getObjectMapper.registerModule(new SimpleModule().addSerializer(classOf[Instant], new InstantSerializer))
  }

  override def run(config: SampleConfig, environment: Environment) : Unit = {
    val jersey = environment.jersey()
    jersey.register(new JsonProcessingExceptionMapper(true))

    val jdbi = new DBIFactory().build(environment, config.database, "mysql")
    jdbi.registerMapper(new ProductResultSetMapperFactory)

    jersey.register(jdbi)

    val users = new RawJdbiUserDao(jdbi)
    jersey.register(users)

    jersey.register(new UsersResource(users))
  }
}
