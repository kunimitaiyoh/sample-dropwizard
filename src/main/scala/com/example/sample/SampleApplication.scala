package com.example.sample

import com.datasift.dropwizard.scala.jdbi.tweak.{OptionContainerFactory, ProductResultSetMapperFactory}
import com.example.sample.api.User
import com.example.sample.dao.UserDao
import com.example.sample.resources.UsersResource
import com.google.common.collect.ImmutableList
import io.dropwizard.Application
import io.dropwizard.db.{DataSourceFactory, PooledDataSourceFactory}
import io.dropwizard.hibernate.{HibernateBundle, SessionFactoryFactory}
import io.dropwizard.jdbi.DBIFactory
import io.dropwizard.migrations.MigrationsBundle
import io.dropwizard.setup.{Bootstrap, Environment}

object SampleApplication extends Application[SampleConfig] {
  val hibernate: HibernateBundle[SampleConfig] = new HibernateBundle[SampleConfig](classOf[User]) {
    override def getDataSourceFactory(configuration: SampleConfig): DataSourceFactory = configuration.database
  }

  def main(args: Array[String]): Unit = {
    run(args:_*)
  }

  override def initialize(bootstrap: Bootstrap[SampleConfig]): Unit = {
    bootstrap.addBundle(new MigrationsBundle[SampleConfig] {
      override def getDataSourceFactory(configuration: SampleConfig): DataSourceFactory = configuration.database

      override def getMigrationsFileName: String = "migrations.sql"
    })

    bootstrap.addBundle(this.hibernate)
  }

  override def run(config: SampleConfig, environment: Environment) : Unit = {
    val jersey = environment.jersey()



    val users = new UserDao(hibernate.getSessionFactory)
    jersey.register(users)

    jersey.register(new UsersResource(users))
  }
}
