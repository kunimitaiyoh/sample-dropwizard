package com.example.sample

import com.example.sample.jdbi.UserDao
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
    val jdbi = new DBIFactory().build(environment, config.database, "mysql")
    environment.jersey().register(jdbi)

    val dao = jdbi.onDemand(classOf[UserDao])
    environment.jersey().register(dao)
  }
}
