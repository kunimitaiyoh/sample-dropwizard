package com.example.sample.migration

import javax.security.auth.login.Configuration

import io.dropwizard
import io.dropwizard.Bundle
import io.dropwizard.db.{DataSourceFactory, DatabaseConfiguration}
import io.dropwizard.migrations.DbCommand
import io.dropwizard.setup.{Bootstrap, Environment}

class GeneratedMigrationsBundle[T <: Configuration](val dataSource: DataSourceFactory) extends DatabaseConfiguration[T] with Bundle {
  val name = "db"

  override def getDataSourceFactory(configuration: T): DataSourceFactory = this.dataSource

  override def initialize(bootstrap: Bootstrap[_ <: dropwizard.Configuration]): Unit = {
    val klass = bootstrap.getApplication.getConfigurationClass.asInstanceOf[Class[T]]
    bootstrap.addCommand(new DbCommand[T](name, this, klass, "migrations.xml"))
  }

  override def run(environment: Environment): Unit = ()
}
