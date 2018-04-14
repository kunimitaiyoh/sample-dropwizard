package com.example.sample

import io.dropwizard.Application
import io.dropwizard.jdbi.DBIFactory
import io.dropwizard.setup.Environment

object SampleApplication extends Application[SampleConfig] {
  def main(args: Array[String]): Unit = {
    run(args:_*)
  }

  override def run(config: SampleConfig, environment: Environment) : Unit = {
    val f = new DBIFactory()
    val jdbi = f.build(environment, config.database, "mysql")

    environment.jersey().register(jdbi)
  }
}
