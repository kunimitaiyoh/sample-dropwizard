package com.example.sample

import com.datasift.dropwizard.scala.jdbi.tweak.{OptionContainerFactory, ProductResultSetMapperFactory}
import com.example.sample.jdbi.UserDao
import com.example.sample.resources.UsersResource
import io.dropwizard.Application
import io.dropwizard.db.DataSourceFactory
import io.dropwizard.jdbi.DBIFactory
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
  }

  override def run(config: SampleConfig, environment: Environment) : Unit = {
    val jersey = environment.jersey()

    val jdbi = new DBIFactory().build(environment, config.database, "mysql")
    jdbi.registerMapper(new ProductResultSetMapperFactory)
    jdbi.registerContainerFactory(new OptionContainerFactory)

    val users = jdbi.onDemand(classOf[UserDao])

    jersey.register(jdbi)
    jersey.register(users)

    jersey.register(new UsersResource(users))
  }
}
