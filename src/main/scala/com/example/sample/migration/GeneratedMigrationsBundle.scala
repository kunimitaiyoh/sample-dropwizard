package com.example.sample.migration

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths}

import com.example.sample.migration.GeneratedMigrationsBundle.GeneratedDbCommand
import io.dropwizard.{Bundle, Configuration}
import io.dropwizard.db.{DataSourceFactory, DatabaseConfiguration}
import io.dropwizard.migrations.DbCommand
import io.dropwizard.setup.{Bootstrap, Environment}
import liquibase.Liquibase
import net.sourceforge.argparse4j.inf.Namespace

class GeneratedMigrationsBundle[T <: Configuration](val dataSource: DataSourceFactory, val changeLog: DatabaseChangeLog) extends DatabaseConfiguration[T] with Bundle {
  val name = "db"

  override def getDataSourceFactory(configuration: T): DataSourceFactory = this.dataSource

  override def initialize(bootstrap: Bootstrap[_]): Unit = {
    val klass = bootstrap.getApplication.getConfigurationClass.asInstanceOf[Class[T]]
    val temp = Paths.get(Paths.get(System.getProperty("java.io.tmpdir")).toString, "migrations.xml")
    bootstrap.addCommand(new GeneratedDbCommand[T](name, this, klass, temp, this.changeLog))
  }

  override def run(environment: Environment): Unit = ()
}

object GeneratedMigrationsBundle {
  class GeneratedDbCommand[T <: Configuration](name: String, strategy: DatabaseConfiguration[T],
    configurationClass: Class[T], migrationsFile: Path, databaseChangeLog: DatabaseChangeLog)
    extends DbCommand[T](name, strategy, configurationClass, migrationsFile.toString) {

    override def run(namespace: Namespace, liquibase: Liquibase): Unit = {
      val mock: String = """<?xml version="1.0" encoding="UTF-8"?>
                           |          <databaseChangeLog
                           |              xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
                           |              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                           |              xmlns:ext="http://www.liquibase.org/xml/ns/dbchangelog-ext"
                           |              xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.1.xsd
                           |              http://www.liquibase.org/xml/ns/dbchangelog-ext http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-ext.xsd">
                           |          </databaseChangeLog>"""

      try {
        Files.write(migrationsFile, mock.getBytes(StandardCharsets.UTF_8))
        super.run(namespace, liquibase)
      } catch {
        case e: Exception => {
          e.printStackTrace()
        }
      } finally {
        Files.delete(migrationsFile)
      }
    }
  }
}
